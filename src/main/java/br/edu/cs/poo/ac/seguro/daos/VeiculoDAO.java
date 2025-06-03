package br.edu.cs.poo.ac.seguro.daos;

import br.edu.cs.poo.ac.seguro.entidades.Apolice;
import br.edu.cs.poo.ac.seguro.entidades.Veiculo;
import br.edu.cesarschool.next.oo.persistenciaobjetos.CadastroObjetos;

import java.io.Serializable;

public class VeiculoDAO extends DAOGenerico<Veiculo>{
    public Class<Veiculo> getClasseEntidade(){
        return Veiculo.class;
    }

}
