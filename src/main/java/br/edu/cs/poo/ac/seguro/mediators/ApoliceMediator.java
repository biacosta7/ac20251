package br.edu.cs.poo.ac.seguro.mediators;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.edu.cs.poo.ac.seguro.entidades.*;

import static br.edu.cs.poo.ac.seguro.mediators.StringUtils.ehNuloOuBranco;

public class ApoliceMediator {
	private static ApoliceMediator apoliceoMediator = ApoliceMediator.getInstancia();
	private static ApoliceMediator instancia = new ApoliceMediator();
	private SeguradoPessoaDAO daoSegPes;
	private SeguradoEmpresaDAO daoSegEmp;
	private VeiculoDAO daoVel;
	private ApoliceDAO daoApo;
	private SinistroDAO daoSin;

	private ApoliceMediator() {}

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
			daoApo = new ApoliceDAO();
			String numeroApolice = gerarNumero(dados.getCpfOuCnpj(), dados.getPlaca());

			SeguradoPessoa seguradoPessoa = null;
			SeguradoEmpresa seguradoEmpresa = null;
			Segurado seguradoAtual = null;
			boolean isEhLocadoraDeVeiculos;

			SeguradoPessoaDAO seguradoPessoaDAO = new SeguradoPessoaDAO();
			SeguradoEmpresaDAO seguradoEmpresaDAO = new SeguradoEmpresaDAO();

			seguradoPessoa = seguradoPessoaDAO.buscar(dados.getCpfOuCnpj());
			if (seguradoPessoa != null) {
				seguradoAtual = seguradoPessoa;
				isEhLocadoraDeVeiculos = false;
			} else {
				seguradoEmpresa = seguradoEmpresaDAO.buscar(dados.getCpfOuCnpj());
				seguradoAtual = seguradoEmpresa;
				isEhLocadoraDeVeiculos = seguradoEmpresa != null && seguradoEmpresa.isEhLocadoraDeVeiculos();
			}

			Veiculo veiculo = daoVel.buscar(dados.getPlaca());
			BigDecimal premio = calcularPremio(seguradoAtual, isEhLocadoraDeVeiculos, dados).setScale(2);
			BigDecimal percentual = new BigDecimal("1.3"); // 130% = 1.3
			BigDecimal vpb = premio.add(seguradoAtual.getBonus().divide(BigDecimal.TEN));
			BigDecimal franquia = percentual.multiply(vpb).setScale(2); // Calcular franquia como 130% de VPB.

			//verificar data se é sempre LocalDate.now()
			Apolice novaApolice = new Apolice(veiculo, franquia, premio, dados.getValorMaximoSegurado(), LocalDate.now());
			novaApolice.setNumero(numeroApolice);

			daoApo.incluir(novaApolice);
			SinistroDAO sinistroDAO = new SinistroDAO();
			Sinistro[] todosSinistros = sinistroDAO.buscarTodos();
			int anoAnterior = LocalDateTime.now().getYear() - 1;

			boolean houveSinistro = false;
			for (Sinistro sin : todosSinistros) {
				Veiculo v = sin.getVeiculo();
				if (v != null) {
					if (sin.getDataHoraSinistro().getYear() == anoAnterior) {
						houveSinistro = true;
						break;
					}
				}
			}

			if (!houveSinistro) {
				BigDecimal bonusAtual = seguradoAtual.getBonus();
				BigDecimal bonusAdicional = premio.multiply(new BigDecimal("0.3")).setScale(2);
				seguradoAtual.creditarBonus(bonusAdicional);

				if (isEhLocadoraDeVeiculos && seguradoAtual instanceof SeguradoEmpresa) {
					seguradoEmpresaDAO.alterar((SeguradoEmpresa) seguradoAtual);
				} else if (!isEhLocadoraDeVeiculos && seguradoAtual instanceof SeguradoPessoa) {
					seguradoPessoaDAO.alterar((SeguradoPessoa) seguradoAtual);
				}
			}

			return new RetornoInclusaoApolice(numeroApolice, null);
		}

		return new RetornoInclusaoApolice(null, msg);
	}

	/*
	 * Ver os testes test19 e test20
	 */
	public Apolice buscarApolice(String numero) {
		daoApo = new ApoliceDAO();
		return daoApo.buscar(numero);
	}
	public String excluirApolice(String numero) {
		if (numero == null || numero.isBlank()){
			return "Número deve ser informado";
		} else if (buscarApolice(numero) == null){
			return "Apólice inexistente";
		}

		Apolice apolice = buscarApolice(numero);

		SinistroDAO daoSin = new SinistroDAO();
		Sinistro[] todosSinistros = daoSin.buscarTodos();

		Veiculo veiApolice = apolice.getVeiculo();

		for(Sinistro sinistro : todosSinistros){
			if(sinistro.getDataHoraSinistro().getYear() == apolice.getDataInicioVigencia().getYear() && sinistro.getVeiculo().equals(veiApolice)){
				return "Existe sinistro cadastrado para o veículo em questão e no mesmo ano da apólice";
			}
		}

		daoApo = new ApoliceDAO();
		daoApo.excluir(numero);

		return null;
	}

	private String validarTodosDadosVeiculo(DadosVeiculo dados) {
		Segurado segurado;

		SeguradoPessoaMediator seguradoPessoaMediator = SeguradoPessoaMediator.getInstancia();
		SeguradoEmpresaMediator empresaMediator = SeguradoEmpresaMediator.getInstancia();
		SeguradoPessoaDAO seguradoPessoaDAO = new SeguradoPessoaDAO();
		SeguradoEmpresaDAO seguradoEmpresaDAO = new SeguradoEmpresaDAO();
		String placa = dados.getPlaca();
		daoApo = new ApoliceDAO();
		daoVel = new VeiculoDAO();
		daoVel = new VeiculoDAO();

		if(dados.getPlaca() == null || dados.getPlaca().isBlank())
			return "Placa do veículo deve ser informada";

		if(dados.getCpfOuCnpj() == null)
			return "CPF ou CNPJ deve ser informado";

		Veiculo veiculo = daoVel.buscar(dados.getPlaca());
		String cpfOuCnpj = dados.getCpfOuCnpj();

		//verificacao de cpf e cnpj
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
			//verificacao categoria e valor
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

			if (dados.getCodigoCategoria() < 1 || dados.getCodigoCategoria() > 5) {
				return "Categoria inválida";
			}
			String numeroApolice = gerarNumero(dados.getCpfOuCnpj(), dados.getPlaca());
			if (daoApo.buscar(numeroApolice) != null) {
				return "Apólice já existente para ano atual e veículo";
			}

		}
		if (cpfOuCnpj.length() == 11) {
			String msgCpf = seguradoPessoaMediator.validarCpf(cpfOuCnpj);
			if (msgCpf != null) {
				return msgCpf;
			}
			seguradoPessoa = seguradoPessoaDAO.buscar(cpfOuCnpj);
			if (seguradoPessoa == null) {
				return "CPF inexistente no cadastro de pessoas";
			}

		} else if (cpfOuCnpj.length() == 14) {
			String msgCnpj = empresaMediator.validarCnpj(cpfOuCnpj);
			if (msgCnpj != null) {
				return msgCnpj;
			}
			seguradoEmpresa = seguradoEmpresaDAO.buscar(cpfOuCnpj);
			if (seguradoEmpresa == null) {
				return "CNPJ inexistente no cadastro de empresas";
			}
			pessoa = false;
		} else {
			return "CPF ou CNPJ deve ser informado";
		}

		if (veiculo != null) {

			Segurado seguradoAtual = null;
			String CpfOuCnpj = null;
			if(dados.getCpfOuCnpj().length() == 11){
				seguradoAtual = veiculo.getProprietarioPessoa();
				CpfOuCnpj = veiculo.getProprietarioPessoa().getCpf();

			} else if (dados.getCpfOuCnpj().length() == 14){
				seguradoAtual = veiculo.getProprietarioEmpresa();
				CpfOuCnpj = veiculo.getProprietarioEmpresa().getCnpj();
			}

			if (seguradoAtual == null || !dados.getCpfOuCnpj().equals(CpfOuCnpj)) {
 				if (dados.getCpfOuCnpj().length() == 11) {
					veiculo.setProprietarioPessoa(seguradoPessoa);
					veiculo.setProprietarioEmpresa(null);
				} else {
					veiculo.setProprietarioEmpresa(seguradoEmpresa);
					veiculo.setProprietarioPessoa(null);
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
		}

		if(pessoa){
			vel = new Veiculo(dados.getPlaca(), dados.getAno(), null, seguradoPessoa, obterCategoriaPorCodigo(dados.getCodigoCategoria()));
		} else{
			vel = new Veiculo(dados.getPlaca(), dados.getAno(), seguradoEmpresa, null, obterCategoriaPorCodigo(dados.getCodigoCategoria()));
		}

		if (veiculo == null)
			daoVel.incluir(vel);
		else
			daoVel.alterar(vel);

		return null;
	}

	private String validarCpfCnpjValorMaximo(DadosVeiculo dados) {
		SeguradoPessoaMediator seguradoPessoaMediator = SeguradoPessoaMediator.getInstancia();
		SeguradoEmpresaMediator empresaMediator = SeguradoEmpresaMediator.getInstancia();

		if(dados.getCpfOuCnpj().length() == 11){
			SeguradoPessoa seg = daoSegPes.buscar(dados.getCpfOuCnpj());
			if (seg != null){
				seguradoPessoaMediator.validarCpf(dados.getCpfOuCnpj());
				BigDecimal valorMax = obterValorMaximoPermitido(dados.getAno(), dados.getCodigoCategoria());
			}
		} else if (dados.getCpfOuCnpj().length() == 14){
			String cnpj = seguradoPessoaMediator.validarCpf(dados.getCpfOuCnpj());
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
		BigDecimal percentual = new BigDecimal("0.03"); // 3%

		BigDecimal vpa = percentual.multiply(dados.getValorMaximoSegurado());
		BigDecimal vpb;

		if (isEhLocadoraDeVeiculos){
			BigDecimal multiplicador = new BigDecimal("1.2");
			vpb = vpa.multiply(multiplicador);
		} else{
			vpb = vpa;
		}

		BigDecimal vpc = vpb.subtract(segurado.getBonus().divide(BigDecimal.TEN));

		if(vpc.compareTo(BigDecimal.ZERO) > 0)
			return vpc;
		else
			return BigDecimal.ZERO;
	}
}