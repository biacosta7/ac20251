package br.edu.cs.poo.ac.seguro.entidades;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Veiculo {
    private String placa;
    private int ano;
    private SeguradoEmpresa proprietarioEmpresa;
    private SeguradoPessoa proprietarioPessoa;
    private CategoriaVeiculo categoria;

    Veiculo(String placa, int ano, SeguradoEmpresa proprietarioEmpresa, SeguradoPessoa proprietarioPessoa, CategoriaVeiculo categoria){
        this.placa = placa;
        this.ano = ano;
        this.proprietarioEmpresa = proprietarioEmpresa;
        this.proprietarioPessoa = proprietarioPessoa;
        this.categoria = categoria;
    }
}
