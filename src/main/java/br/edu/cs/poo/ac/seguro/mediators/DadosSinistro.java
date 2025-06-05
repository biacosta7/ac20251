package br.edu.cs.poo.ac.seguro.mediators;

import java.time.LocalDateTime;

public class DadosSinistro {
    private String placa;
    private LocalDateTime dataHoraSinistro;
    private String usuarioRegistro;
    private double valorSinistro;
    private int codigoTipoSinistro;

    public DadosSinistro(String placa, LocalDateTime dataHoraSinistro, String usuarioRegistro, double valorSinistro, int codigoTipoSinistro) {
        this.placa = placa;
        this.dataHoraSinistro = dataHoraSinistro;
        this.usuarioRegistro = usuarioRegistro;
        this.valorSinistro = valorSinistro;
        this.codigoTipoSinistro = codigoTipoSinistro;
    }
    public String getPlaca() {
        return placa;
    }
    public void setPlaca(String placa) {
        this.placa = placa;
    }
    public LocalDateTime getDataHoraSinistro() {
        return dataHoraSinistro;
    }
    public void setDataHoraSinistro(LocalDateTime dataHoraSinistro) {
        this.dataHoraSinistro = dataHoraSinistro;
    }
    public String getUsuarioRegistro() {
        return usuarioRegistro;
    }
    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }
    public double getValorSinistro() {
        return valorSinistro;
    }
    public void setValorSinistro(double valorSinistro) {
        this.valorSinistro = valorSinistro;
    }
    public int getCodigoTipoSinistro() {
        return codigoTipoSinistro;
    }
    public void setCodigoTipoSinistro(int codigoTipoSinistro) {
        this.codigoTipoSinistro = codigoTipoSinistro;
    }
}
