package br.edu.cs.poo.ac.seguro.mediators;

import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;

import static br.edu.cs.poo.ac.seguro.mediators.StringUtils.ehNuloOuBranco;
import static br.edu.cs.poo.ac.seguro.mediators.StringUtils.temSomenteNumeros;

public class SeguradoEmpresaMediator {
    private static SeguradoEmpresaMediator instancia = new SeguradoEmpresaMediator();
    private SeguradoMediator seguradoMediator = SeguradoMediator.getInstancia();
    private SeguradoEmpresaDAO dao = new SeguradoEmpresaDAO();

    private SeguradoEmpresaMediator() {
    }

    public static SeguradoEmpresaMediator getInstancia() {
        return instancia;
    }

    public boolean calcularCnpjValido(String cnpj) {
        if (cnpj.equals("00000000000000") || cnpj.equals("11111111111111") ||
                cnpj.equals("22222222222222") || cnpj.equals("33333333333333") ||
                cnpj.equals("44444444444444") || cnpj.equals("55555555555555") ||
                cnpj.equals("66666666666666") || cnpj.equals("77777777777777") ||
                cnpj.equals("88888888888888") || cnpj.equals("99999999999999")) {
            return false;
        }

        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;

        for (int i = 0; i < 12; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos1[i];
        }

        int resto = soma % 11;
        int verificador1 = (resto < 2) ? 0 : 11 - resto;

        if (verificador1 != (cnpj.charAt(12) - '0')) {
            return false;
        }

        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        soma = 0;

        for (int i = 0; i < 13; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos2[i];
        }

        resto = soma % 11;
        int verificador2 = (resto < 2) ? 0 : 11 - resto;

        if (verificador2 != (cnpj.charAt(13) - '0')) {
            return false;
        }

        return true;
    }

    public String validarCnpj(String cnpj) {
        //if (!ehCnpjValido(cnpj)) {
            //return "CNPJ inválido.";
        //

        if (ehNuloOuBranco(cnpj)) {
            return "CNPJ deve ser informado";
        }

        if(cnpj.length() != 14){
            return "CNPJ deve ter 14 caracteres";
        }

        if(!temSomenteNumeros(cnpj)){
            return "CNPJ com dígito inválido";
        }

        if(!calcularCnpjValido(cnpj)){
            return "CNPJ com dígito inválido";
        }

        return null;
    }

    public String validarFaturamento(double faturamento) {
        if (faturamento <= 0) {
            return "Faturamento deve ser maior que zero";
        }
        return null;
    }

    public String incluirSeguradoEmpresa(SeguradoEmpresa seg) {
        String msg = validarSeguradoEmpresa(seg);
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        if (dao.buscar(seg.getCnpj()) != null) {
            return "CNPJ do segurado empresa já existente";
        }
        boolean sucesso = dao.incluir(seg);
        if (!sucesso) {
            return "Erro ao incluir segurado empresa.";
        }
        return null; 
    }

    public String alterarSeguradoEmpresa(SeguradoEmpresa seg) {
        String msg = validarSeguradoEmpresa(seg);
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        if (dao.buscar(seg.getCnpj()) == null) {
            return "CNPJ do segurado empresa não existente";
        }
        boolean sucesso = dao.alterar(seg);
        if (!sucesso) {
            return "Erro ao alterar segurado empresa.";
        }
        return null;
    }

    public String excluirSeguradoEmpresa(String cnpj) {
        if (dao.buscar(cnpj) == null) {
            return "CNPJ do segurado empresa não existente";
        }
        boolean sucesso = dao.excluir(cnpj);
        if (!sucesso) {
            return "Erro ao excluir segurado empresa.";
        }
        return null;
    }

    public SeguradoEmpresa buscarSeguradoEmpresa(String cnpj) {
        return dao.buscar(cnpj);
    }

    public String validarSeguradoEmpresa(SeguradoEmpresa seg) {
        if (seg == null) {
            return "Segurado inválido.";
        }
        String msg = seguradoMediator.validarNome(seg.getNome());
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        msg = seguradoMediator.validarEndereco(seg.getEndereco());
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        msg = seguradoMediator.validarDataCriacao(seg.getDataAbertura());
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        msg = validarCnpj(seg.getCnpj());
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        msg = validarFaturamento(seg.getFaturamento());
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        return null;
    }
}
