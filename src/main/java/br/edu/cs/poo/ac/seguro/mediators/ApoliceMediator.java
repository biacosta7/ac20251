package br.edu.cs.poo.ac.seguro.mediators;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList; // Importe ArrayList
import java.util.Arrays;    // Importe Arrays
import java.util.List;      // Importe List
import java.util.stream.Collectors; // Importe Collectors

import br.edu.cs.poo.ac.seguro.daos.*;
import br.edu.cs.poo.ac.seguro.entidades.*;

import static br.edu.cs.poo.ac.seguro.mediators.StringUtils.ehNuloOuBranco;

public class ApoliceMediator {
	// Note: a instância Singleton deve ser única. apoliceoMediator e instancia estão redundantes.
	// Mantenha apenas uma instância estática e o método getInstancia.
	private static ApoliceMediator instancia = new ApoliceMediator();
	private SeguradoPessoaDAO daoSegPes;
	private SeguradoEmpresaDAO daoSegEmp;
	private VeiculoDAO daoVel;
	private ApoliceDAO daoApo;
	private SinistroDAO daoSin; // Melhor inicializar no construtor ou em cada método se for para ser fresco

	private ApoliceMediator() {
		// Inicialize os DAOs aqui para garantir que não sejam nulos ou recriados desnecessariamente
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
			// Os DAOs devem ser inicializados no construtor Singleton
			// daoApo = new ApoliceDAO(); // Remova esta linha se inicializar no construtor
			String numeroApolice = gerarNumero(dados.getCpfOuCnpj(), dados.getPlaca());

			SeguradoPessoa seguradoPessoa = null;
			SeguradoEmpresa seguradoEmpresa = null;
			Segurado seguradoAtual = null;
			boolean isEhLocadoraDeVeiculos;

			// Os DAOs devem ser usados a partir das variáveis de instância
			// SeguradoPessoaDAO seguradoPessoaDAO = new SeguradoPessoaDAO(); // Remova esta linha
			// SeguradoEmpresaDAO seguradoEmpresaDAO = new SeguradoEmpresaDAO(); // Remova esta linha

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
			BigDecimal premio = calcularPremio(seguradoAtual, isEhLocadoraDeVeiculos, dados).setScale(2);
			BigDecimal percentual = new BigDecimal("1.3"); // 130% = 1.3
			BigDecimal vpb = premio.add(seguradoAtual.getBonus().divide(BigDecimal.TEN));
			BigDecimal franquia = percentual.multiply(vpb).setScale(2); // Calcular franquia como 130% de VPB.

			//verificar data se é sempre LocalDate.now()
			Apolice novaApolice = new Apolice(numeroApolice, veiculo, franquia, premio, dados.getValorMaximoSegurado(), LocalDate.now());

			daoApo.incluir(novaApolice);
			// SinistroDAO sinistroDAO = new SinistroDAO(); // Remova esta linha

			// CORREÇÃO AQUI: Converter Registro[] para Sinistro[]
			Registro[] todosSinistrosRegistros = daoSin.buscarTodos(); // Isso deve retornar Registro[]
			// Converte o array de Registro[] para List<Sinistro> e depois para Sinistro[]
			List<Sinistro> listaSinistros = Arrays.stream(todosSinistrosRegistros)
					.filter(s -> s instanceof Sinistro) // Garantia de tipo
					.map(s -> (Sinistro) s)
					.collect(Collectors.toList());
			Sinistro[] todosSinistros = listaSinistros.toArray(new Sinistro[0]); // Converte a lista de volta para array de Sinistro

			int anoAnterior = LocalDateTime.now().getYear() - 1;

			boolean houveSinistro = false;
			for (Sinistro sin : todosSinistros) {
				Veiculo v = sin.getVeiculo();
				if (v != null) {
					// Melhorar esta condição: um sinistro cobre um veículo.
					// Aparentemente, a intenção é ver se HOUVE SINISTRO PARA O VEÍCULO DA APÓLICE NO ANO ANTERIOR.
					// Se a apólice é para um VEÍCULO, o sinistro também precisa ser associado a esse VEÍCULO.
					// E também ao proprietário atual.
					if (sin.getDataHoraSinistro().getYear() == anoAnterior && sin.getVeiculo().equals(veiculo)) { // Adicione sin.getVeiculo().equals(veiculo)
						houveSinistro = true;
						break;
					}
				}
			}

			if (!houveSinistro) {
				// BigDecimal bonusAtual = seguradoAtual.getBonus(); // Não é necessário buscar, já tem seguradoAtual
				BigDecimal bonusAdicional = premio.multiply(new BigDecimal("0.3")).setScale(2);
				seguradoAtual.creditarBonus(bonusAdicional);

				if (isEhLocadoraDeVeiculos && seguradoAtual instanceof SeguradoEmpresa) {
					daoSegEmp.alterar((SeguradoEmpresa) seguradoAtual); // Use a variável de instância
				} else if (!isEhLocadoraDeVeiculos && seguradoAtual instanceof SeguradoPessoa) {
					daoSegPes.alterar((SeguradoPessoa) seguradoAtual); // Use a variável de instância
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
		// daoApo = new ApoliceDAO(); // Remova esta linha se inicializar no construtor
		return daoApo.buscar(numero);
	}
	public String excluirApolice(String numero) {
		if (numero == null || numero.isBlank()){
			return "Número deve ser informado";
		} else if (buscarApolice(numero) == null){
			return "Apólice inexistente";
		}

		Apolice apolice = buscarApolice(numero);

		// SinistroDAO daoSin = new SinistroDAO(); // Remova esta linha

		// CORREÇÃO AQUI: Converter Registro[] para Sinistro[]
		Registro[] todosSinistrosRegistros = daoSin.buscarTodos();
		List<Sinistro> listaSinistros = Arrays.stream(todosSinistrosRegistros)
				.filter(s -> s instanceof Sinistro)
				.map(s -> (Sinistro) s)
				.collect(Collectors.toList());
		// Não precisa converter para array de volta se for apenas para iterar.
		// O loop for-each funciona com List<Sinistro>.

		Veiculo veiApolice = apolice.getVeiculo();

		for(Sinistro sinistro : listaSinistros){ // Use a lista de sinistros
			if(sinistro.getDataHoraSinistro().getYear() == apolice.getDataInicioVigencia().getYear() && sinistro.getVeiculo().equals(veiApolice)){
				return "Existe sinistro cadastrado para o veículo em questão e no mesmo ano da apólice";
			}
		}

		// daoApo = new ApoliceDAO(); // Remova esta linha
		daoApo.excluir(numero);

		return null;
	}

	private String validarTodosDadosVeiculo(DadosVeiculo dados) {
		// A inicialização dos DAOs deve ser no construtor. Remova as seguintes linhas
		// SeguradoPessoaDAO seguradoPessoaDAO = new SeguradoPessoaDAO();
		// SeguradoEmpresaDAO seguradoEmpresaDAO = new SeguradoEmpresaDAO();
		// daoApo = new ApoliceDAO();
		// daoVel = new VeiculoDAO();
		// daoVel = new VeiculoDAO(); // Duplicado

		String placa = dados.getPlaca();

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


		if (veiculo == null){ // Se o veículo não existe no DAO, estamos criando um novo
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

			// A validação de categoria inválida já é coberta pelo categoria == null
			// if (dados.getCodigoCategoria() < 1 || dados.getCodigoCategoria() > 5) {
			//    return "Categoria inválida";
			// }

			String numeroApolice = gerarNumero(dados.getCpfOuCnpj(), dados.getPlaca());
			if (daoApo.buscar(numeroApolice) != null) {
				return "Apólice já existente para ano atual e veículo";
			}
		}

		// Lógica para buscar segurado (pessoa ou empresa)
		if (cpfOuCnpj.length() == 11) {
			String msgCpf = SeguradoPessoaMediator.getInstancia().validarCpf(cpfOuCnpj);
			if (msgCpf != null) {
				if(msgCpf.equals("CPF com dígito inválido")){
					return "CPF inválido";
				}
				return msgCpf;
			}
			seguradoPessoa = daoSegPes.buscar(cpfOuCnpj); // Use a variável de instância
			if (seguradoPessoa == null) {
				return "CPF inexistente no cadastro de pessoas";
			}

		} else if (cpfOuCnpj.length() == 14) {
			String msgCnpj = SeguradoEmpresaMediator.getInstancia().validarCnpj(cpfOuCnpj); // Use instância Singleton
			if (msgCnpj != null) {
				if(msgCnpj.equals("CNPJ com dígito inválido")){
					return "CNPJ inválido";
				}
				return msgCnpj;
			}
			seguradoEmpresa = daoSegEmp.buscar(cpfOuCnpj); // Use a variável de instância
			if (seguradoEmpresa == null) {
				return "CNPJ inexistente no cadastro de empresas";
			}
			pessoa = false;
		} else {
			return "CPF ou CNPJ deve ser informado";
		}

		// Lógica para veículo existente
		if (veiculo != null) { // Se o veículo já existe no DAO, estamos atualizando seu proprietário se necessário.
			Segurado proprietarioAtualDoVeiculo = null;

			// Propriedade proprietarioPessoa e proprietarioEmpresa no Veiculo não são diretamente visíveis na classe Veiculo que você me enviou.
			// Assumindo que você tem um método getProprietario() que retorna um Segurado, ou que existe uma lógica para determinar o tipo.
			// Vou adaptar com base no que presumo que você tem.
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

			// Se o proprietário do veículo existente é diferente do proprietário informado, atualiza.
			if (proprietarioAtualDoVeiculo == null || !dados.getCpfOuCnpj().equals(cpfOuCnpjProprietarioAtual)) {
				if (dados.getCpfOuCnpj().length() == 11) {
					veiculo.setProprietario(seguradoPessoa); // Assumindo setProprietario(Segurado)
				} else {
					veiculo.setProprietario(seguradoEmpresa); // Assumindo setProprietario(Segurado)
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
				vel = new Veiculo(dados.getPlaca(), dados.getAno(), seguradoPessoa, obterCategoriaPorCodigo(dados.getCodigoCategoria())); // Ajuste no construtor do Veiculo
			} else{
				vel = new Veiculo(dados.getPlaca(), dados.getAno(), seguradoEmpresa, obterCategoriaPorCodigo(dados.getCodigoCategoria())); // Ajuste no construtor do Veiculo
			}
			daoVel.incluir(vel); // Inclui o novo veículo
		}

		return null;
	}

	private String validarCpfCnpjValorMaximo(DadosVeiculo dados) {
		// Esta função parece ter lógica redundante e não usada diretamente.
		// O `validarTodosDadosVeiculo` já faz as validações de CPF/CNPJ e valor máximo.
		// Recomendo revisar se esta função é realmente necessária e onde ela seria chamada.
		// Por ora, vou corrigir apenas para compilar.

		// SeguradoPessoaMediator seguradoPessoaMediator = SeguradoPessoaMediator.getInstancia(); // Use instancia
		// SeguradoEmpresaMediator empresaMediator = SeguradoEmpresaMediator.getInstancia(); // Use instancia

		if(dados.getCpfOuCnpj().length() == 11){
			SeguradoPessoa seg = daoSegPes.buscar(dados.getCpfOuCnpj()); // Use a variável de instância
			if (seg != null){
				SeguradoPessoaMediator.getInstancia().validarCpf(dados.getCpfOuCnpj());
				BigDecimal valorMax = obterValorMaximoPermitido(dados.getAno(), dados.getCodigoCategoria());
				// Faltaria uma comparação ou uso do valorMax aqui
			}
		} else if (dados.getCpfOuCnpj().length() == 14){
			String cnpjValidado = SeguradoEmpresaMediator.getInstancia().validarCnpj(dados.getCpfOuCnpj()); // Correção: validarCnpj retorna String, não String
			// if (cnpjValidado != null) { return cnpjValidado; } // Adicione validação aqui
			BigDecimal valorMax = obterValorMaximoPermitido(dados.getAno(), dados.getCodigoCategoria());
			// Faltaria uma comparação ou uso do valorMax aqui
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

		// A linha abaixo usa segurado.getBonus().divide(BigDecimal.TEN), mas BigDecimal.TEN não existe.
		// Suponho que seja para dividir por 10.
		// Além disso, o bônus deve ser subtraído, não dividido por 10 para então subtrair.
		// Se o bônus for um valor monetário, subtraia-o diretamente.
		// Se for um percentual do bônus, use o percentual.
		// Vou assumir que é para subtrair o bônus dividido por 10.
		BigDecimal vpc = vpb.subtract(segurado.getBonus().divide(BigDecimal.valueOf(10))); // Use BigDecimal.valueOf(10)

		if(vpc.compareTo(BigDecimal.ZERO) > 0)
			return vpc;
		else
			return BigDecimal.ZERO;
	}
}