package br.edu.cs.poo.ac.seguro.daos;

import br.edu.cesarschool.next.oo.persistenciaobjetos.CadastroObjetos;
import br.edu.cs.poo.ac.seguro.entidades.Registro;
import br.edu.cs.poo.ac.seguro.entidades.Sinistro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SinistroDAO extends DAOGenerico<Sinistro> {
    public Class<Sinistro> getClasseEntidade(){
        return Sinistro.class;
    }

    public List<Sinistro> buscarTodosLista() {
        Registro[] todosOsRegistros = super.buscarTodos();
        List<Sinistro> listaSinistros = new ArrayList<>();
        for (Registro reg : todosOsRegistros) {
            if (reg instanceof Sinistro) {
                listaSinistros.add((Sinistro) reg);
            }
        }
        return listaSinistros;
    }
}
