package br.edu.cs.poo.ac.seguro.mediators;

import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;
import br.edu.cs.poo.ac.seguro.daos.SeguradoEmpresaDAO;

public class SeguradoEmpresaMediator {
    private static SeguradoEmpresaMediator instancia = new SeguradoEmpresaMediator();
    private SeguradoMediator seguradoMediator = SeguradoMediator.getInstancia();
    private SeguradoEmpresaDAO dao = new SeguradoEmpresaDAO();

    private SeguradoEmpresaMediator() {
    }

    public static SeguradoEmpresaMediator getInstancia() {
        return instancia;
    }

    public String validarCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) {
            return "CNPJ inválido.";
        }
        return null;
    }

    public String validarFaturamento(double faturamento) {
        if (faturamento < 0) {
            return "Faturamento inválido.";
        }
        return null;
    }

    public String incluirSeguradoEmpresa(SeguradoEmpresa seg) {
        String msg = validarSeguradoEmpresa(seg);
        if (msg != null) {
            return msg;
        }
        boolean sucesso = dao.incluir(seg);
        if (!sucesso) {
            return "Erro ao incluir segurado empresa.";
        }
        return null;
    }

    public String alterarSeguradoEmpresa(SeguradoEmpresa seg) {
        String msg = validarSeguradoEmpresa(seg);
        if (msg != null) {
            return msg;
        }
        boolean sucesso = dao.alterar(seg);
        if (!sucesso) {
            return "Erro ao alterar segurado empresa.";
        }
        return null;
    }

    public String excluirSeguradoEmpresa(String cnpj) {
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
        if (msg != null) {
            return msg;
        }
        msg = seguradoMediator.validarEndereco(seg.getEndereco());
        if (msg != null) {
            return msg;
        }
        msg = seguradoMediator.validarDataCriacao(seg.getDataAbertura());
        if (msg != null) {
            return msg;
        }
        msg = validarCnpj(seg.getCnpj());
        if (msg != null) {
            return msg;
        }
        msg = validarFaturamento(seg.getFaturamento());
        if (msg != null) {
            return msg;
        }
        return null;
    }
}
