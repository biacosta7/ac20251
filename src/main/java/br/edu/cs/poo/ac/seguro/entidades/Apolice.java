package br.edu.cs.poo.ac.seguro.entidades;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

import java.math.BigDecimal;

@Getter @Setter
public class Apolice implements Serializable {
    private static final long serialVersionUID = 1L;
    private Veiculo veiculo;
    private BigDecimal valorFranquia;
    private BigDecimal valorPremio;
    private BigDecimal valorMaximoSegurado;
    private String numero;

    public Apolice(Veiculo veiculo, BigDecimal valorFranquia, BigDecimal valorPremio, BigDecimal valorMaximoSegurado){
        this.veiculo = veiculo;
        this.valorFranquia = valorFranquia;
        this.valorPremio = valorPremio;
        this.valorMaximoSegurado = valorMaximoSegurado;
    }
}
