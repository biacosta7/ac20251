package br.edu.cs.poo.ac.seguro.daos;

import br.edu.cesarschool.next.oo.persistenciaobjetos.CadastroObjetos;
import br.edu.cs.poo.ac.seguro.entidades.Registro;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        if (serializables == null || serializables.length == 0) {
            return new Registro[0];
        }
        List<Registro> listaOrdenada = Arrays.stream(serializables)
                .map(s -> (Registro) s)
                .sorted(Comparator.comparing(Registro::getIdUnico))
                .collect(Collectors.toList());
        return listaOrdenada.toArray(new Registro[0]);
    }
}