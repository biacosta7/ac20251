package br.edu.cs.poo.ac.seguro.mediators;

import java.math.BigDecimal;
import java.math.RoundingMode; // Import RoundingMode
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import br.edu.cs.poo.ac.seguro.daos.*;
import br.edu.cs.poo.ac.seguro.entidades.*;

import static br.edu.cs.poo.ac.seguro.mediators.StringUtils.ehNuloOuBranco;

public class ApoliceMediator {
	private static final ApoliceMediator instancia = new ApoliceMediator();

	private SeguradoPessoaDAO daoSegPes;
	private SeguradoEmpresaDAO daoSegEmp;
	private VeiculoDAO daoVel;
	private ApoliceDAO daoApo;
	private SinistroDAO daoSin;

	private ApoliceMediator() {
		this.daoSegPes = new SeguradoPessoaDAO();
		this.daoSegEmp = new SeguradoEmpresaDAO();
		this.daoVel = new VeiculoDAO();
		this.daoApo = new ApoliceDAO();
		this.daoSin = new SinistroDAO();
	}

	public static ApoliceMediator getInstancia() {
		return instancia;
	}

	private CategoriaVeiculo obterCategoriaPorCodigo(int codigo) {
		for (CategoriaVeiculo categoria : CategoriaVeiculo.values()) {
			if (categoria.getCodigo() == codigo) {
				return categoria;
			}
		}
		return null;
	}

	public BigDecimal obterValorCarroParaAno(CategoriaVeiculo categoria, int ano) {
		if (categoria == null || categoria.getPrecosAnos() == null) {
			return BigDecimal.ZERO;
		}
		for (PrecoAno precoAno : categoria.getPrecosAnos()) {
			if (precoAno.getAno() == ano) {
				return BigDecimal.valueOf(precoAno.getPreco());
			}
		}
		return BigDecimal.ZERO;
	}

	public RetornoInclusaoApolice incluirApolice(DadosVeiculo dados) {
		if (dados == null) {
			return new RetornoInclusaoApolice(null, "Dados do veículo devem ser informados.");
		}

		String msgValidacao = validarTodosDadosVeiculo(dados);
		if (msgValidacao != null) {
			return new RetornoInclusaoApolice(null, msgValidacao);
		}

		String numeroApolice = gerarNumero(dados.getCpfOuCnpj(), dados.getPlaca());

		Segurado seguradoAtual;
		SeguradoPessoa seguradoPessoa = daoSegPes.buscar(dados.getCpfOuCnpj());
		SeguradoEmpresa seguradoEmpresa = null;

		boolean isEhLocadoraDeVeiculos = false;

		if (seguradoPessoa != null) {
			seguradoAtual = seguradoPessoa;
		} else {
			seguradoEmpresa = daoSegEmp.buscar(dados.getCpfOuCnpj());
			if (seguradoEmpresa != null) {
				seguradoAtual = seguradoEmpresa;
				isEhLocadoraDeVeiculos = seguradoEmpresa.isEhLocadoraDeVeiculos();
			} else {
				return new RetornoInclusaoApolice(null, "Segurado (CPF/CNPJ) não encontrado.");
			}
		}

		Veiculo veiculo = daoVel.buscar(dados.getPlaca());
		if (veiculo == null) {
			// This should ideally not happen if validarTodosDadosVeiculo correctly creates/updates vehicle
			return new RetornoInclusaoApolice(null, "Erro interno: Veículo não encontrado após validação.");
		}

		// --- FIX START ---
		// Ensure all BigDecimal operations specify a RoundingMode for setScale() and divide()
		// Line 88 is here:
		BigDecimal premio = calcularPremio(seguradoAtual, isEhLocadoraDeVeiculos, dados).setScale(2, RoundingMode.HALF_UP);
		BigDecimal percentualFranquia = new BigDecimal("1.3");

		// The 'divide' method below already has scale and rounding mode, but re-confirm
		//seguradoAtual.getBonus().divide(BigDecimal.valueOf(10), 2, BigDecimal.ROUND_HALF_UP)
		// Ensure seguradoAtual.getBonus() is not null before using it in divide
		BigDecimal bonusForCalculation = seguradoAtual.getBonus() != null ? seguradoAtual.getBonus() : BigDecimal.ZERO;

		BigDecimal vpb = premio.add(bonusForCalculation.divide(BigDecimal.valueOf(10), 2, RoundingMode.HALF_UP));

		// This is where the error likely originates if premio or vpb calculation results in more than 2 decimals
		BigDecimal franquia = percentualFranquia.multiply(vpb).setScale(2, RoundingMode.HALF_UP); // Ensure this setScale has RoundingMode
		// --- FIX END ---

		Apolice novaApolice = new Apolice(numeroApolice, veiculo, franquia, premio, dados.getValorMaximoSegurado(), LocalDate.now());

		daoApo.incluir(novaApolice);

		List<Sinistro> todosSinistros = Arrays.stream(daoSin.buscarTodos())
				.filter(s -> s instanceof Sinistro)
				.map(s -> (Sinistro) s)
				.collect(Collectors.toList());

		int anoAnterior = LocalDate.now().getYear() - 1;
		boolean houveSinistroParaVeiculoNoAnoAnterior = false;

		for (Sinistro sin : todosSinistros) {
			if (sin.getVeiculo() != null && sin.getVeiculo().equals(veiculo) &&
					sin.getDataHoraSinistro().getYear() == anoAnterior) {
				houveSinistroParaVeiculoNoAnoAnterior = true;
				break;
			}
		}

		if (!houveSinistroParaVeiculoNoAnoAnterior) {
			BigDecimal bonusAdicional = premio.multiply(new BigDecimal("0.3")).setScale(2, RoundingMode.HALF_UP); // Ensure RoundingMode
			seguradoAtual.creditarBonus(bonusAdicional);

			if (seguradoAtual instanceof SeguradoEmpresa) {
				daoSegEmp.alterar((SeguradoEmpresa) seguradoAtual);
			} else if (seguradoAtual instanceof SeguradoPessoa) {
				daoSegPes.alterar((SeguradoPessoa) seguradoAtual);
			}
		}

		return new RetornoInclusaoApolice(numeroApolice, null); // Set mensagemErro to null for success
	}

	public Apolice buscarApolice(String numero) {
		return daoApo.buscar(numero);
	}

	public String excluirApolice(String numero) {
		if (numero == null || numero.isBlank()) {
			return "Número deve ser informado";
		}
		Apolice apolice = buscarApolice(numero);
		if (apolice == null) {
			return "Apólice inexistente";
		}

		List<Sinistro> todosSinistros = Arrays.stream(daoSin.buscarTodos())
				.filter(s -> s instanceof Sinistro)
				.map(s -> (Sinistro) s)
				.collect(Collectors.toList());

		Veiculo veiApolice = apolice.getVeiculo();
		int anoApolice = apolice.getDataInicioVigencia().getYear();

		for (Sinistro sinistro : todosSinistros) {
			if (sinistro.getVeiculo() != null && sinistro.getVeiculo().equals(veiApolice) &&
					sinistro.getDataHoraSinistro().getYear() == anoApolice) {
				return "Existe sinistro cadastrado para o veículo em questão e no mesmo ano da apólice.";
			}
		}

		daoApo.excluir(numero);
		return null;
	}

	private String validarTodosDadosVeiculo(DadosVeiculo dados) {
		if (ehNuloOuBranco(dados.getPlaca())) {
			return "Placa do veículo deve ser informada.";
		}
		String placa = dados.getPlaca().trim();
		if (placa.isEmpty()) {
			return "Placa do veículo deve ser informada.";
		}

		if (ehNuloOuBranco(dados.getCpfOuCnpj())) {
			return "CPF ou CNPJ deve ser informado.";
		}
		String cpfOuCnpj = dados.getCpfOuCnpj().trim();

		boolean isPessoaFisica = false;
		Segurado seguradoEncontrado = null;

		if (cpfOuCnpj.length() == 11) {
			String msgCpf = SeguradoPessoaMediator.getInstancia().validarCpf(cpfOuCnpj);
			if (msgCpf != null) {
				if (msgCpf.equals("CPF com dígito inválido")) {
					return "CPF inválido.";
				}
				return msgCpf;
			}
			seguradoEncontrado = daoSegPes.buscar(cpfOuCnpj);
			if (seguradoEncontrado == null) {
				return "CPF inexistente no cadastro de pessoas.";
			}
			isPessoaFisica = true;
		} else if (cpfOuCnpj.length() == 14) {
			String msgCnpj = SeguradoEmpresaMediator.getInstancia().validarCnpj(cpfOuCnpj);
			if (msgCnpj != null) {
				if (msgCnpj.equals("CNPJ com dígito inválido")) {
					return "CNPJ inválido.";
				}
				return msgCnpj;
			}
			seguradoEncontrado = daoSegEmp.buscar(cpfOuCnpj);
			if (seguradoEncontrado == null) {
				return "CNPJ inexistente no cadastro de empresas.";
			}
		} else {
			return "CPF ou CNPJ deve ter 11 ou 14 dígitos.";
		}

		Veiculo veiculoExistente = daoVel.buscar(placa);

		if (veiculoExistente == null) {
			int ano = dados.getAno();
			if (ano < 2020 || ano > 2025) {
				return "Ano do veículo tem que estar entre 2020 e 2025, incluindo estes.";
			}

			BigDecimal valorMaximoSegurado = dados.getValorMaximoSegurado();
			if (valorMaximoSegurado == null || valorMaximoSegurado.compareTo(BigDecimal.ZERO) <= 0) {
				return "Valor máximo segurado deve ser informado e maior que zero.";
			}

			CategoriaVeiculo categoria = obterCategoriaPorCodigo(dados.getCodigoCategoria());
			if (categoria == null) {
				return "Categoria inválida.";
			}

			BigDecimal valorCarroBase = obterValorCarroParaAno(categoria, ano);
			if (valorCarroBase.compareTo(BigDecimal.ZERO) == 0) {
				return "Valor do carro não encontrado para o ano e categoria informados.";
			}

			BigDecimal limiteInferior = valorCarroBase.multiply(new BigDecimal("0.75"));
			BigDecimal limiteSuperior = valorCarroBase.multiply(new BigDecimal("1.00"));
			if (valorMaximoSegurado.compareTo(limiteInferior) < 0 || valorMaximoSegurado.compareTo(limiteSuperior) > 0) {
				return "Valor máximo segurado deve estar entre 75% e 100% do valor do carro para a categoria e ano.";
			}

			String numeroApolicePotencial = gerarNumero(cpfOuCnpj, placa);
			if (daoApo.buscar(numeroApolicePotencial) != null) {
				return "Já existe uma apólice cadastrada para este veículo no ano atual.";
			}

			Veiculo novoVeiculo;
			if (isPessoaFisica) {
				novoVeiculo = new Veiculo(placa, ano, (SeguradoPessoa) seguradoEncontrado, categoria);
			} else {
				novoVeiculo = new Veiculo(placa, ano, (SeguradoEmpresa) seguradoEncontrado, categoria);
			}
			daoVel.incluir(novoVeiculo);
		} else {
			String cpfOuCnpjProprietarioAtual = null;
			if (veiculoExistente.getProprietario() instanceof SeguradoPessoa) {
				cpfOuCnpjProprietarioAtual = ((SeguradoPessoa) veiculoExistente.getProprietario()).getCpf();
			} else if (veiculoExistente.getProprietario() instanceof SeguradoEmpresa) {
				cpfOuCnpjProprietarioAtual = ((SeguradoEmpresa) veiculoExistente.getProprietario()).getCnpj();
			}

			// Note: 'proprietarioAtualDoVeiculo' was undefined in your original snippet,
			// Assuming it's meant to be the owner of the existing vehicle.
			// I'll use veiculoExistente.getProprietario() for consistency.
			if (veiculoExistente.getProprietario() == null || !cpfOuCnpj.equals(cpfOuCnpjProprietarioAtual)) {
				if (isPessoaFisica) {
					veiculoExistente.setProprietario((SeguradoPessoa) seguradoEncontrado);
				} else {
					veiculoExistente.setProprietario((SeguradoEmpresa) seguradoEncontrado);
				}
				daoVel.alterar(veiculoExistente);
			}

			if (dados.getValorMaximoSegurado() == null || dados.getValorMaximoSegurado().compareTo(BigDecimal.ZERO) <= 0) {
				return "Valor máximo segurado inválido.";
			}

			String numeroApolicePotencial = gerarNumero(cpfOuCnpj, placa);
			if (daoApo.buscar(numeroApolicePotencial) != null) {
				return "Apólice já existente para ano atual e veículo";
			}
		}
		return null;
	}

	private BigDecimal obterValorMaximoPermitido(int ano, int codigoCat) {
		return obterValorCarroParaAno(obterCategoriaPorCodigo(codigoCat), ano);
	}

	private String gerarNumero(String cpfOuCnpj, String placa) {
		String anoAtual = String.valueOf(LocalDate.now().getYear());
		if (cpfOuCnpj.length() == 11) {
			return anoAtual + "000" + cpfOuCnpj + placa;
		} else if (cpfOuCnpj.length() == 14) {
			return anoAtual + cpfOuCnpj + placa;
		}
		return null;
	}

	private BigDecimal calcularPremio(Segurado segurado, boolean isEhLocadoraDeVeiculos, DadosVeiculo dados) {
		BigDecimal percentual = new BigDecimal("0.03"); // 3%

		BigDecimal vpa = percentual.multiply(dados.getValorMaximoSegurado());
		BigDecimal vpb;

		if (isEhLocadoraDeVeiculos) {
			BigDecimal multiplicador = new BigDecimal("1.2");
			vpb = vpa.multiply(multiplicador);
		} else {
			vpb = vpa;
		}

		BigDecimal bonusDeduction = BigDecimal.ZERO;
		// Ensure segurado.getBonus() is not null before operations
		if (segurado.getBonus() != null) {
			// Apply scale and rounding mode for division
			bonusDeduction = segurado.getBonus().divide(BigDecimal.valueOf(10), 2, RoundingMode.HALF_UP);
		}

		// Apply scale and rounding mode for subtraction if necessary, or just ensure final result has it.
		BigDecimal vpc = vpb.subtract(bonusDeduction);

		if (vpc.compareTo(BigDecimal.ZERO) > 0)
			return vpc;
		else
			return BigDecimal.ZERO;
	}
}