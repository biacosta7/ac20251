package br.edu.cs.poo.ac.seguro.entidades;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class Veiculo implements Serializable, Registro{
    private static final long serialVersionUID = 1L;
    private String placa;
    private int ano;
    private Segurado proprietario;
    private CategoriaVeiculo categoria;

    public Veiculo(String placa, int ano, CategoriaVeiculo categoria, Segurado proprietario){
        this.placa = placa;
        this.ano = ano;
        this.proprietario = proprietario;
        this.categoria = categoria;
    }

    public String getIdUnico(){
        return placa;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || !(obj instanceof Veiculo)) {
            return false;
        }

        Veiculo outro = (Veiculo) obj;

        if (this.placa != null && this.placa.equals(outro.placa)){
            return true;
        }
        return false;
    }
}
