package br.edu.cs.poo.ac.seguro.daos;

import br.edu.cs.poo.ac.seguro.entidades.Segurado;

public abstract class SeguradoDAO<T extends Segurado> extends DAOGenerico<T> {
    // Aqui podem ir métodos comuns para todos os tipos de Segurado
}
