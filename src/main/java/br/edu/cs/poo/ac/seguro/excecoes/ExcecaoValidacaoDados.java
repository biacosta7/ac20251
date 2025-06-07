package br.edu.cs.poo.ac.seguro.excecoes;

import java.util.ArrayList;
import java.util.List;

public class ExcecaoValidacaoDados extends Exception {
    private List<String> mensagens;

    public ExcecaoValidacaoDados() {
        super("Erros de validação encontrados");
        this.mensagens = new ArrayList<>();
    }

    public ExcecaoValidacaoDados(String mensagem) {
        super(mensagem);
        this.mensagens = new ArrayList<>();
        this.mensagens.add(mensagem);
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void adicionarMensagem(String mensagem) {
        this.mensagens.add(mensagem);
    }

    public boolean temErros() {
        return !mensagens.isEmpty();
    }
}