package br.edu.cs.poo.ac.seguro.excecoes;

import java.util.ArrayList;
import java.util.List;

public class ExcecaoValidacaoDados extends Exception {
    private List<String> mensagens;

    public ExcecaoValidacaoDados(List<String> mensagens) {
        this.mensagens = new ArrayList<>(mensagens); // Garante que a lista é uma cópia
    }

    public List<String> getMensagens() {
        return mensagens;
    }
}