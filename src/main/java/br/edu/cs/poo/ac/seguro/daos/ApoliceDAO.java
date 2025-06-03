package br.edu.cs.poo.ac.seguro.daos;

import br.edu.cesarschool.next.oo.persistenciaobjetos.CadastroObjetos;
import br.edu.cs.poo.ac.seguro.entidades.Apolice;
import br.edu.cs.poo.ac.seguro.entidades.Segurado;

import java.io.Serializable;

public class ApoliceDAO extends DAOGenerico<Apolice> {
    public Class<Apolice> getClasseEntidade(){
        return Apolice.class;
    }
}
