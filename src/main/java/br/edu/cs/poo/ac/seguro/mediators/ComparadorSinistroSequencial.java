package br.edu.cs.poo.ac.seguro.mediators;

import br.edu.cs.poo.ac.seguro.entidades.Sinistro;

import java.util.Comparator;

public class ComparadorSinistroSequencial implements Comparator<Sinistro> {
    @Override
    public int compare(Sinistro s1, Sinistro s2) {
        return Integer.compare(s1.getSequencial(), s2.getSequencial());
    }
}