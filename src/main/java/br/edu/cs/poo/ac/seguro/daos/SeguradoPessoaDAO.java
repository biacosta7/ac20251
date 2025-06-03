package br.edu.cs.poo.ac.seguro.daos;

import br.edu.cesarschool.next.oo.persistenciaobjetos.CadastroObjetos;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa;

/*
 * As classes Segurado e SeguradoPessoa devem implementar Serializable.
 */
public class SeguradoPessoaDAO extends SeguradoDAO<SeguradoPessoa> {

	public SeguradoPessoa buscar(String numero){
		return (SeguradoPessoa) super.buscar(numero);
	}

	public Class<SeguradoPessoa> getClasseEntidade(){
		return SeguradoPessoa.class;
	}
	
}
