package br.edu.cs.poo.ac.seguro.daos;

import br.edu.cesarschool.next.oo.persistenciaobjetos.CadastroObjetos;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;

public class SeguradoEmpresaDAO extends DAOGenerico{
    public SeguradoEmpresaDAO() {
        cadastro = new CadastroObjetos(SeguradoEmpresa.class);
    }

    public SeguradoEmpresa buscar(String cnpj) {
        return (SeguradoEmpresa)cadastro.buscar(cnpj);
    }

    public boolean incluir(SeguradoEmpresa segurado) {
        if (buscar(segurado.getCnpj()) != null) {
            return false;
        } else {
            cadastro.incluir(segurado, segurado.getCnpj());
            return true;
        }
    }
    public boolean alterar(SeguradoEmpresa segurado) {
        if (buscar(segurado.getCnpj()) == null) {
            return false;
        } else {
            cadastro.alterar(segurado, segurado.getCnpj());
            return true;
        }
    }
    public boolean excluir(String cpf) {
        if (buscar(cpf) == null) {
            return false;
        } else {
            cadastro.excluir(cpf);
            return true;
        }
    }
}
