package br.edu.cs.poo.ac.seguro.entidades;

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

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public SeguradoEmpresa getProprietarioEmpresa() {
        return proprietarioEmpresa;
    }

    public void setProprietarioEmpresa(SeguradoEmpresa proprietarioEmpresa) {
        this.proprietarioEmpresa = proprietarioEmpresa;
    }

    public SeguradoPessoa getProprietarioPessoa() {
        return proprietarioPessoa;
    }

    public void setProprietarioPessoa(SeguradoPessoa proprietarioPessoa) {
        this.proprietarioPessoa = proprietarioPessoa;
    }

    public CategoriaVeiculo getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaVeiculo categoria) {
        this.categoria = categoria;
    }
}
