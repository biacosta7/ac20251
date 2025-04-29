package br.edu.cs.poo.ac.seguro.testes;

import br.edu.cesarschool.next.oo.persistenciaobjetos.CadastroObjetos;
import br.edu.cs.poo.ac.seguro.daos.DAOGenerico;
import br.edu.cs.poo.ac.seguro.entidades.Apolice;

import java.io.Serializable;

public class ApoliceDAO extends DAOGenerico {
    public ApoliceDAO() {
        cadastro = new CadastroObjetos(Apolice.class);
    }
    public Apolice buscar(String numero) {
        return (Apolice)cadastro.buscar(numero);
    }

    public boolean incluir(Apolice segurado) {
        if (buscar(segurado.getNumero()) != null) {
            return false;
        } else {
            cadastro.incluir((Serializable)segurado, segurado.getNumero());
            return true;
        }
    }
    public boolean alterar(Apolice segurado) {
        if (buscar(segurado.getNumero()) == null) {
            return false;
        } else {
            cadastro.alterar((Serializable)segurado, segurado.getNumero());
            return true;
        }
    }
    public boolean excluir(String numero) {
        if (buscar(numero) == null) {
            return false;
        } else {
            cadastro.excluir(numero);
            return true;
        }
    }
}
