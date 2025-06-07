package br.edu.cs.poo.ac.seguro.mediators;

import java.math.BigDecimal;
import java.math.RoundingMode; // Make sure this import is present
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
	private static ApoliceMediator instancia = new ApoliceMediator();
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
		for (PrecoAno precoAno : categoria.getPrecosAnos()) {
			if (precoAno.getAno() == ano) {
				return BigDecimal.valueOf(precoAno.getPreco());
			}
		}
		return BigDecimal.ZERO;
	}

	public RetornoInclusaoApolice incluirApolice(DadosVeiculo dados) {
		if (dados == null) {
			return new RetornoInclusaoApolice(null, "Dados do veículo devem ser informados");
		}
		String msg = validarTodosDadosVeiculo(dados);

		if(msg == null){
			String numeroApolice = gerarNumero(dados.getCpfOuCnpj(), dados.getPlaca());

			SeguradoPessoa seguradoPessoa = null;
			SeguradoEmpresa seguradoEmpresa = null;
			Segurado seguradoAtual = null;
			boolean isEhLocadoraDeVeiculos;

			seguradoPessoa = daoSegPes.buscar(dados.getCpfOuCnpj());
			if (seguradoPessoa != null) {
				seguradoAtual = seguradoPessoa;
				isEhLocadoraDeVeiculos = false;
			} else {
				seguradoEmpresa = daoSegEmp.buscar(dados.getCpfOuCnpj());
				seguradoAtual = seguradoEmpresa;
				isEhLocadoraDeVeiculos = seguradoEmpresa != null && seguradoEmpresa.isEhLocadoraDeVeiculos();
			}

			Veiculo veiculo = daoVel.buscar(dados.getPlaca());

			// Define a common scale and rounding mode for consistency
			int scale = 2;
			RoundingMode roundingMode = RoundingMode.HALF_UP;

			BigDecimal premio = calcularPremio(seguradoAtual, isEhLocadoraDeVeiculos, dados).setScale(scale, roundingMode);
			BigDecimal percentual = new BigDecimal("1.3"); // 130% = 1.3

			// FIX: Apply setScale and RoundingMode to the division here
			// This line was causing the error previously
			BigDecimal bonusDividido = seguradoAtual.getBonus().divide(BigDecimal.TEN, scale, roundingMode);
			BigDecimal vpb = premio.add(bonusDividido);

			BigDecimal franquia = percentual.multiply(vpb).setScale(scale, roundingMode); // Calcular franquia como 130% de VPB.

			Apolice novaApolice = new Apolice(numeroApolice, veiculo, franquia, premio, dados.getValorMaximoSegurado(), LocalDate.now());

			daoApo.incluir(novaApolice);

			Registro[] todosSinistrosRegistros = daoSin.buscarTodos();
			List<Sinistro> listaSinistros = Arrays.stream(todosSinistrosRegistros)
					.filter(s -> s instanceof Sinistro)
					.map(s -> (Sinistro) s)
					.collect(Collectors.toList());

			int anoAnterior = LocalDateTime.now().getYear() - 1;

			boolean houveSinistro = false;
			for (Sinistro sin : listaSinistros) {
				Veiculo v = sin.getVeiculo();
				if (v != null) {
					if (sin.getDataHoraSinistro().getYear() == anoAnterior && sin.getVeiculo().equals(veiculo)) {
						houveSinistro = true;
						break;
					}
				}
			}

			if (!houveSinistro) {
				BigDecimal bonusAdicional = premio.multiply(new BigDecimal("0.3")).setScale(scale, roundingMode);
				seguradoAtual.creditarBonus(bonusAdicional);

				if (isEhLocadoraDeVeiculos && seguradoAtual instanceof SeguradoEmpresa) {
					daoSegEmp.alterar((SeguradoEmpresa) seguradoAtual);
				} else if (!isEhLocadoraDeVeiculos && seguradoAtual instanceof SeguradoPessoa) {
					daoSegPes.alterar((SeguradoPessoa) seguradoAtual);
				}
			}

			return new RetornoInclusaoApolice(numeroApolice, null);
		}

		return new RetornoInclusaoApolice(null, msg);
	}

	public Apolice buscarApolice(String numero) {
		return daoApo.buscar(numero);
	}
	public String excluirApolice(String numero) {
		if (numero == null || numero.isBlank()){
			return "Número deve ser informado";
		} else if (buscarApolice(numero) == null){
			return "Apólice inexistente";
		}

		Apolice apolice = buscarApolice(numero);

		Registro[] todosSinistrosRegistros = daoSin.buscarTodos();
		List<Sinistro> listaSinistros = Arrays.stream(todosSinistrosRegistros)
				.filter(s -> s instanceof Sinistro)
				.map(s -> (Sinistro) s)
				.collect(Collectors.toList());

		Veiculo veiApolice = apolice.getVeiculo();

		for(Sinistro sinistro : listaSinistros){
			if(sinistro.getDataHoraSinistro().getYear() == apolice.getDataInicioVigencia().getYear() && sinistro.getVeiculo().equals(veiApolice)){
				return "Existe sinistro cadastrado para o veículo em questão e no mesmo ano da apólice";
			}
		}

		daoApo.excluir(numero);

		return null;
	}

	private String validarTodosDadosVeiculo(DadosVeiculo dados) {
		String placa = dados.getPlaca();

		if(dados.getPlaca() == null || dados.getPlaca().isBlank())
			return "Placa do veículo deve ser informada";

		if(dados.getCpfOuCnpj() == null)
			return "CPF ou CNPJ deve ser informado";

		Veiculo veiculo = daoVel.buscar(dados.getPlaca());
		String cpfOuCnpj = dados.getCpfOuCnpj();

		if (ehNuloOuBranco(cpfOuCnpj)) {
			return "CPF ou CNPJ deve ser informado";
		}
		cpfOuCnpj = cpfOuCnpj.trim();

		Veiculo vel = null;
		boolean pessoa = true;
		SeguradoPessoa seguradoPessoa = null;
		SeguradoEmpresa seguradoEmpresa = null;


		if (veiculo == null){
			if(ehNuloOuBranco(placa)){
				return "Placa do veículo deve ser informada";
			}
			placa = placa.trim();
			if(placa.isEmpty()){
				return "Placa do veículo deve ser informada";
			}

			int ano = dados.getAno();
			if(ano < 2020 || ano > 2025){
				return "Ano tem que estar entre 2020 e 2025, incluindo estes";
			}
			BigDecimal valorMaximoSegurado = dados.getValorMaximoSegurado();
			if(valorMaximoSegurado == null){
				return "Valor máximo segurado deve ser informado";
			}
			CategoriaVeiculo categoria = obterCategoriaPorCodigo(dados.getCodigoCategoria());
			if(categoria == null){
				return "Categoria inválida";
			}
			BigDecimal valorCarro = obterValorCarroParaAno(categoria, ano);
			if(valorCarro.compareTo(BigDecimal.ZERO) == 0){
				return "Valor do carro não encontrado para o ano";
			}
			BigDecimal limiteInferior = valorCarro.multiply(new BigDecimal("0.75"));
			BigDecimal limiteSuperior = valorCarro.multiply(new BigDecimal("1.00"));
			if (valorMaximoSegurado.compareTo(limiteInferior) < 0 || valorMaximoSegurado.compareTo(limiteSuperior) > 0) {
				return "Valor máximo segurado deve estar entre 75% e 100% do valor do carro encontrado na categoria";
			}

			String numeroApolice = gerarNumero(dados.getCpfOuCnpj(), dados.getPlaca());
			if (daoApo.buscar(numeroApolice) != null) {
				return "Apólice já existente para ano atual e veículo";
			}
		}

		if (cpfOuCnpj.length() == 11) {
			String msgCpf = SeguradoPessoaMediator.getInstancia().validarCpf(cpfOuCnpj);
			if (msgCpf != null) {
				if(msgCpf.equals("CPF com dígito inválido")){
					return "CPF inválido";
				}
				return msgCpf;
			}
			seguradoPessoa = daoSegPes.buscar(cpfOuCnpj);
			if (seguradoPessoa == null) {
				return "CPF inexistente no cadastro de pessoas";
			}

		} else if (cpfOuCnpj.length() == 14) {
			String msgCnpj = SeguradoEmpresaMediator.getInstancia().validarCnpj(cpfOuCnpj);
			if (msgCnpj != null) {
				if(msgCnpj.equals("CNPJ com dígito inválido")){
					return "CNPJ inválido";
				}
				return msgCnpj;
			}
			seguradoEmpresa = daoSegEmp.buscar(cpfOuCnpj);
			if (seguradoEmpresa == null) {
				return "CNPJ inexistente no cadastro de empresas";
			}
			pessoa = false;
		} else {
			return "CPF ou CNPJ deve ser informado";
		}

		if (veiculo != null) {
			Segurado proprietarioAtualDoVeiculo = null;

			if (veiculo.getProprietario() instanceof SeguradoPessoa) {
				proprietarioAtualDoVeiculo = (SeguradoPessoa) veiculo.getProprietario();
			} else if (veiculo.getProprietario() instanceof SeguradoEmpresa) {
				proprietarioAtualDoVeiculo = (SeguradoEmpresa) veiculo.getProprietario();
			}

			String cpfOuCnpjProprietarioAtual = null;
			if (proprietarioAtualDoVeiculo instanceof SeguradoPessoa) {
				cpfOuCnpjProprietarioAtual = ((SeguradoPessoa) proprietarioAtualDoVeiculo).getCpf();
			} else if (proprietarioAtualDoVeiculo instanceof SeguradoEmpresa) {
				cpfOuCnpjProprietarioAtual = ((SeguradoEmpresa) proprietarioAtualDoVeiculo).getCnpj();
			}

			if (proprietarioAtualDoVeiculo == null || !dados.getCpfOuCnpj().equals(cpfOuCnpjProprietarioAtual)) {
				if (dados.getCpfOuCnpj().length() == 11) {
					veiculo.setProprietario(seguradoPessoa);
				} else {
					veiculo.setProprietario(seguradoEmpresa);
				}
				daoVel.alterar(veiculo);
			}

			if (dados.getValorMaximoSegurado() == null || dados.getValorMaximoSegurado().compareTo(BigDecimal.ZERO) <= 0) {
				return "Valor máximo segurado inválido.";
			}

			String numeroApolice = gerarNumero(dados.getCpfOuCnpj(), dados.getPlaca());
			if (daoApo.buscar(numeroApolice) != null) {
				return "Apólice já existente para ano atual e veículo";
			}
		} else {
			if(pessoa){
				vel = new Veiculo(dados.getPlaca(), dados.getAno(), seguradoPessoa, obterCategoriaPorCodigo(dados.getCodigoCategoria()));
			} else{
				vel = new Veiculo(dados.getPlaca(), dados.getAno(), seguradoEmpresa, obterCategoriaPorCodigo(dados.getCodigoCategoria()));
			}
			daoVel.incluir(vel);
		}

		return null;
	}

	private String validarCpfCnpjValorMaximo(DadosVeiculo dados) {
		if(dados.getCpfOuCnpj().length() == 11){
			SeguradoPessoa seg = daoSegPes.buscar(dados.getCpfOuCnpj());
			if (seg != null){
				SeguradoPessoaMediator.getInstancia().validarCpf(dados.getCpfOuCnpj());
				BigDecimal valorMax = obterValorMaximoPermitido(dados.getAno(), dados.getCodigoCategoria());
			}
		} else if (dados.getCpfOuCnpj().length() == 14){
			String cnpjValidado = SeguradoEmpresaMediator.getInstancia().validarCnpj(dados.getCpfOuCnpj());
			BigDecimal valorMax = obterValorMaximoPermitido(dados.getAno(), dados.getCodigoCategoria());
		}

		return null;
	}

	private BigDecimal obterValorMaximoPermitido(int ano, int codigoCat) {
		for (CategoriaVeiculo categoria : CategoriaVeiculo.values()) {
			if (categoria.getCodigo() == codigoCat) {
				for (PrecoAno precoAno : categoria.getPrecosAnos()) {
					if (precoAno.getAno() == ano) {
						return BigDecimal.valueOf(precoAno.getPreco());
					}
				}
			}
		}
		return BigDecimal.ZERO;
	}

	private String gerarNumero(String cpfOuCnpj, String placa) {
		String anoAtual = String.valueOf(LocalDate.now().getYear());
		if(cpfOuCnpj.length() == 11){
			return anoAtual + "000" + cpfOuCnpj + placa;
		} else if (cpfOuCnpj.length() == 14){
			return anoAtual + cpfOuCnpj + placa;
		}
		return null;
	}

	private BigDecimal calcularPremio(Segurado segurado, boolean isEhLocadoraDeVeiculos, DadosVeiculo dados){
		int scale = 2;
		RoundingMode roundingMode = RoundingMode.HALF_UP;

		BigDecimal percentual = new BigDecimal("0.03");

		BigDecimal vpa = percentual.multiply(dados.getValorMaximoSegurado());
		BigDecimal vpb;

		if (isEhLocadoraDeVeiculos){
			BigDecimal multiplicador = new BigDecimal("1.2");
			vpb = vpa.multiply(multiplicador);
		} else{
			vpb = vpa;
		}

		BigDecimal bonusDivididoPorDez = segurado.getBonus().divide(BigDecimal.TEN, scale, roundingMode);

		BigDecimal vpc = vpb.subtract(bonusDivididoPorDez);

		if(vpc.compareTo(BigDecimal.ZERO) > 0)
			return vpc.setScale(scale, roundingMode);
		else
			return BigDecimal.ZERO.setScale(scale, roundingMode);
	}
}