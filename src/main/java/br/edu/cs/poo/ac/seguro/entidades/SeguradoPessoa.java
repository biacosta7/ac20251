package br.edu.cs.poo.ac.seguro.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.Serializable;

public class SeguradoPessoa extends Segurado implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String cpf;
    private double renda;

    public SeguradoPessoa(String nome, Endereco endereco, LocalDate dataNascimento, BigDecimal bonus, String cpf, double renda) {
        super(nome, endereco, dataNascimento, bonus);
        this.cpf = cpf;
        this.renda = renda;
    }
    public String getCpf(){
        return cpf;
    }
    public double getRenda(){
        return renda;
    }
    public void setRenda(double renda){
        this.renda = renda;
    }
    public LocalDate getDataNascimento(){
        return this.getDataCriacao();
    }
    public void setDataNascimento(LocalDate dataNascimento){
        this.setDataCriacao(dataNascimento);
    }
}
