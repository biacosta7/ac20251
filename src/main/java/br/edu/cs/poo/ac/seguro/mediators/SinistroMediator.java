package br.edu.cs.poo.ac.seguro.mediators;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.cs.poo.ac.seguro.daos.ApoliceDAO;
import br.edu.cs.poo.ac.seguro.daos.SinistroDAO;
import br.edu.cs.poo.ac.seguro.daos.VeiculoDAO;
import br.edu.cs.poo.ac.seguro.entidades.Apolice;
import br.edu.cs.poo.ac.seguro.entidades.Registro;
import br.edu.cs.poo.ac.seguro.entidades.Sinistro;
import br.edu.cs.poo.ac.seguro.entidades.TipoSinistro;
import br.edu.cs.poo.ac.seguro.entidades.Veiculo;
import br.edu.cs.poo.ac.seguro.excecoes.ExcecaoValidacaoDados;

public class SinistroMediator {

    private VeiculoDAO daoVeiculo = new VeiculoDAO();
    private ApoliceDAO daoApolice = new ApoliceDAO();
    private SinistroDAO daoSinistro = new SinistroDAO();

    private static SinistroMediator instancia;

    public static SinistroMediator getInstancia() {
        if (instancia == null)
            instancia = new SinistroMediator();
        return instancia;
    }

    private SinistroMediator() {}

    public String incluirSinistro(DadosSinistro dados, LocalDateTime dataHoraAtual) throws ExcecaoValidacaoDados {
        ExcecaoValidacaoDados excecao = new ExcecaoValidacaoDados();
        Veiculo veiculo = null;

        if (dados == null) {
            excecao.adicionarMensagem("Dados do sinistro devem ser informados");
            throw excecao;
        }

        if (dados.getDataHoraSinistro() == null) {
            excecao.adicionarMensagem("Data/hora do sinistro deve ser informada");
        } else if (!dados.getDataHoraSinistro().isBefore(dataHoraAtual)) {
            excecao.adicionarMensagem("Data/hora do sinistro deve ser menor que a data/hora atual");
        }

        if (StringUtils.ehNuloOuBranco(dados.getPlaca())) {
            excecao.adicionarMensagem("Placa do Veículo deve ser informada");
        } else {
            veiculo = daoVeiculo.buscar(dados.getPlaca());
            if (veiculo == null) {
                excecao.adicionarMensagem("Veículo não cadastrado");
            }
        }

        if (StringUtils.ehNuloOuBranco(dados.getUsuarioRegistro())) {
            excecao.adicionarMensagem("Usuário do registro de sinistro deve ser informado");
        }

        if (dados.getValorSinistro() <= 0) {
            excecao.adicionarMensagem("Valor do sinistro deve ser maior que zero");
        }

        TipoSinistro tipoSinistroSelecionado = TipoSinistro.getTipoSinistro(dados.getCodigoTipoSinistro());
        if (tipoSinistroSelecionado == null) {
            excecao.adicionarMensagem("Código do tipo de sinistro inválido");
        }

        if (excecao.temErros()) {
            throw excecao;
        }

        Registro[] todasAsApolicesRegistros = daoApolice.buscarTodos();
        List<Apolice> todasAsApolices = new ArrayList<>();
        for(Registro reg : todasAsApolicesRegistros){
            if(reg instanceof Apolice){
                todasAsApolices.add((Apolice) reg);
            }
        }

        Apolice apoliceCobrindo = null;
        for (Apolice apolice : todasAsApolices) {
            if (apolice.getVeiculo() != null && apolice.getVeiculo().getPlaca().equals(dados.getPlaca())) {
                LocalDateTime inicioVigenciaPolicy = apolice.getDataInicioVigencia().atStartOfDay();
                LocalDateTime fimVigenciaPolicy = apolice.getDataInicioVigencia().plusYears(1).atStartOfDay();

                if (!dados.getDataHoraSinistro().isBefore(inicioVigenciaPolicy) &&
                        dados.getDataHoraSinistro().isBefore(fimVigenciaPolicy)) {
                    apoliceCobrindo = apolice;
                    break;
                }
            }
        }

        if (apoliceCobrindo == null) {
            excecao.adicionarMensagem("Não existe apólice vigente para o veículo");
            throw excecao;
        }

        BigDecimal valorSinistroBd = new BigDecimal(dados.getValorSinistro());
        BigDecimal valorMaximoSeguradoBd = apoliceCobrindo.getValorMaximoSegurado();

        if (valorSinistroBd.compareTo(valorMaximoSeguradoBd) > 0) {
            excecao.adicionarMensagem("Valor do sinistro não pode ultrapassar o valor máximo segurado constante na apólice");
            throw excecao;
        }

        List<Sinistro> sinistrosExistentes = daoSinistro.buscarTodosLista();
        List<Sinistro> sinistrosMesmoNumeroApolice = new ArrayList<>();

        for (Sinistro s : sinistrosExistentes) {
            if (apoliceCobrindo.getNumero().equals(s.getNumeroApolice())) {
                sinistrosMesmoNumeroApolice.add(s);
            }
        }

        int novoSequencial = 1;
        if (!sinistrosMesmoNumeroApolice.isEmpty()) {
            Collections.sort(sinistrosMesmoNumeroApolice, new ComparadorSinistroSequencial());
            novoSequencial = sinistrosMesmoNumeroApolice.get(sinistrosMesmoNumeroApolice.size() - 1).getSequencial() + 1;
        }

        String numeroSinistro = "S" + apoliceCobrindo.getNumero() + String.format("%03d", novoSequencial);

        Sinistro novoSinistro = new Sinistro(
                numeroSinistro,
                veiculo,
                dados.getDataHoraSinistro(),
                dataHoraAtual,
                dados.getUsuarioRegistro(),
                valorSinistroBd,
                tipoSinistroSelecionado
        );

        novoSinistro.setNumeroApolice(apoliceCobrindo.getNumero());
        novoSinistro.setSequencial(novoSequencial);

        boolean inclusaoBemSucedida = daoSinistro.incluir(novoSinistro);
        if (!inclusaoBemSucedida) {

            excecao.adicionarMensagem("Erro ao incluir o sinistro no DAO.");
            throw excecao;
        }

        return novoSinistro.getNumero();
    }

    public void alterarSinistro(String numeroSinistro, DadosSinistro dadosAtualizados, LocalDateTime dataHoraAtual) throws ExcecaoValidacaoDados {
        if (StringUtils.ehNuloOuBranco(numeroSinistro)) {
            throw new ExcecaoValidacaoDados("Número do sinistro deve ser informado para alteração.");
        }

        Sinistro sinistroExistente = daoSinistro.buscar(numeroSinistro);
        if (sinistroExistente == null) {
            throw new ExcecaoValidacaoDados("Sinistro não encontrado para alteração.");
        }

        // Podemos reusar parte da validação do incluir, ou fazer validações específicas
        // por simplicidade, vamos validar data, valor e tipo, e placa

        ExcecaoValidacaoDados excecao = new ExcecaoValidacaoDados();
        Veiculo veiculo = null;

        if (dadosAtualizados == null) {
            excecao.adicionarMensagem("Dados do sinistro devem ser informados");
            throw excecao;
        }

        if (dadosAtualizados.getDataHoraSinistro() == null) {
            excecao.adicionarMensagem("Data/hora do sinistro deve ser informada");
        } else if (!dadosAtualizados.getDataHoraSinistro().isBefore(dataHoraAtual)) {
            excecao.adicionarMensagem("Data/hora do sinistro deve ser menor que a data/hora atual");
        }

        if (StringUtils.ehNuloOuBranco(dadosAtualizados.getPlaca())) {
            excecao.adicionarMensagem("Placa do Veículo deve ser informada");
        } else {
            veiculo = daoVeiculo.buscar(dadosAtualizados.getPlaca());
            if (veiculo == null) {
                excecao.adicionarMensagem("Veículo não cadastrado");
            }
        }

        if (StringUtils.ehNuloOuBranco(dadosAtualizados.getUsuarioRegistro())) {
            excecao.adicionarMensagem("Usuário do registro de sinistro deve ser informado");
        }

        if (dadosAtualizados.getValorSinistro() <= 0) {
            excecao.adicionarMensagem("Valor do sinistro deve ser maior que zero");
        }

        TipoSinistro tipoSinistroSelecionado = TipoSinistro.getTipoSinistro(dadosAtualizados.getCodigoTipoSinistro());
        if (tipoSinistroSelecionado == null) {
            excecao.adicionarMensagem("Código do tipo de sinistro inválido");
        }

        if (excecao.temErros()) {
            throw excecao;
        }

        // Validação de apólice vigente para o veículo e sinistro (semelhante ao incluir)
        Registro[] todasAsApolicesRegistros = daoApolice.buscarTodos();
        List<Apolice> todasAsApolices = new ArrayList<>();
        for(Registro reg : todasAsApolicesRegistros){
            if(reg instanceof Apolice){
                todasAsApolices.add((Apolice) reg);
            }
        }

        Apolice apoliceCobrindo = null;
        for (Apolice apolice : todasAsApolices) {
            if (apolice.getVeiculo() != null && apolice.getVeiculo().getPlaca().equals(dadosAtualizados.getPlaca())) {
                LocalDateTime inicioVigenciaPolicy = apolice.getDataInicioVigencia().atStartOfDay();
                LocalDateTime fimVigenciaPolicy = apolice.getDataInicioVigencia().plusYears(1).atStartOfDay();

                if (!dadosAtualizados.getDataHoraSinistro().isBefore(inicioVigenciaPolicy) &&
                        dadosAtualizados.getDataHoraSinistro().isBefore(fimVigenciaPolicy)) {
                    apoliceCobrindo = apolice;
                    break;
                }
            }
        }

        if (apoliceCobrindo == null) {
            excecao.adicionarMensagem("Não existe apólice vigente para o veículo");
            throw excecao;
        }

        BigDecimal valorSinistroBd = new BigDecimal(dadosAtualizados.getValorSinistro());
        BigDecimal valorMaximoSeguradoBd = apoliceCobrindo.getValorMaximoSegurado();

        if (valorSinistroBd.compareTo(valorMaximoSeguradoBd) > 0) {
            excecao.adicionarMensagem("Valor do sinistro não pode ultrapassar o valor máximo segurado constante na apólice");
            throw excecao;
        }

        // Atualiza os dados do sinistro existente
        sinistroExistente.setVeiculo(veiculo);
        sinistroExistente.setDataHoraSinistro(dadosAtualizados.getDataHoraSinistro());
        sinistroExistente.setUsuarioRegistro(dadosAtualizados.getUsuarioRegistro());
        sinistroExistente.setValorSinistro(valorSinistroBd);
        sinistroExistente.setTipo(tipoSinistroSelecionado);

        boolean alteracaoBemSucedida = daoSinistro.alterar(sinistroExistente);
        if (!alteracaoBemSucedida) {
            throw new ExcecaoValidacaoDados("Erro ao alterar o sinistro no DAO.");
        }
    }

    // Método para excluir sinistro pelo número
    public void excluirSinistro(String numeroSinistro) throws ExcecaoValidacaoDados {
        if (StringUtils.ehNuloOuBranco(numeroSinistro)) {
            throw new ExcecaoValidacaoDados("Número do sinistro deve ser informado para exclusão.");
        }

        Sinistro sinistroExistente = daoSinistro.buscar(numeroSinistro);
        if (sinistroExistente == null) {
            throw new ExcecaoValidacaoDados("Sinistro não encontrado para exclusão.");
        }

        boolean exclusaoBemSucedida = daoSinistro.excluir(numeroSinistro);
        if (!exclusaoBemSucedida) {
            throw new ExcecaoValidacaoDados("Erro ao excluir o sinistro no DAO.");
        }
    }

    // Método para buscar sinistro pelo número
    public Sinistro buscarSinistro(String numeroSinistro) throws ExcecaoValidacaoDados {
        if (StringUtils.ehNuloOuBranco(numeroSinistro)) {
            throw new ExcecaoValidacaoDados("Número do sinistro deve ser informado para busca.");
        }

        Sinistro sinistro = daoSinistro.buscar(numeroSinistro);
        if (sinistro == null) {
            throw new ExcecaoValidacaoDados("Sinistro não encontrado.");
        }

        return sinistro;
    }

    // Opcional: método para listar todos os sinistros
    public List<Sinistro> listarTodosSinistros() {
        return daoSinistro.buscarTodosLista();
    }
}