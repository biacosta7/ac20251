package br.edu.cs.poo.ac.seguro.daos;

import br.edu.cesarschool.next.oo.persistenciaobjetos.CadastroObjetos;
import br.edu.cs.poo.ac.seguro.entidades.Sinistro;

import java.io.Serializable;

public class SinistroDAO extends DAOGenerico<Sinistro> {
    public Class<Sinistro> getClasseEntidade(){
        return Sinistro.class;
    }

}
