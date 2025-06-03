package br.edu.cs.poo.ac.seguro.daos;

import br.edu.cesarschool.next.oo.persistenciaobjetos.CadastroObjetos;
import br.edu.cs.poo.ac.seguro.entidades.Registro;
import br.edu.cs.poo.ac.seguro.entidades.Sinistro;

import java.io.Serializable;

public abstract class DAOGenerico <T extends Registro>{
    private CadastroObjetos cadastro;
    public abstract Class<T> getClasseEntidade();

    public DAOGenerico() {
        cadastro = new CadastroObjetos(getClasseEntidade());
    }

    // CRUD
    public T buscar(String id) {
        return (T)cadastro.buscar(id);
    }

    public boolean incluir(T registro) {
        if (buscar(registro.getIdUnico()) != null) {
            return false;
        } else {
            cadastro.incluir((Serializable) registro, registro.getIdUnico());
            return true;
        }
    }

    public boolean alterar(T registro) {
        if (buscar(registro.getIdUnico()) == null) {
            return false;
        } else {
            cadastro.alterar((Serializable) registro, registro.getIdUnico());
            return true;
        }
    }

    public boolean excluir(String id) {
        if (buscar(id) == null) {
            return false;
        } else {
            cadastro.excluir(id);
            return true;
        }
    }

    public Registro[] buscarTodos() {
        Serializable[] serializables = cadastro.buscarTodos();
        Registro[] registros = new Registro[serializables.length];
        for (int i = 0; i < serializables.length; i++) {
            registros[i] = (Registro) serializables[i];
        }
        return registros;
    }
}