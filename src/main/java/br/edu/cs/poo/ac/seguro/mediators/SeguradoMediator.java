package br.edu.cs.poo.ac.seguro.mediators;

import br.edu.cs.poo.ac.seguro.entidades.Endereco;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SeguradoMediator {
    private static SeguradoMediator instancia = new SeguradoMediator();
    private SeguradoMediator() {
    }
    public static SeguradoMediator getInstancia() {
        return instancia;
    }

    public String validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return "Nome inválido.";
        }
        return null;
    }

    public String validarEndereco(Endereco endereco) {
        if (endereco == null) {
            return "Endereço inválido.";
        }
        return null;
    }

    public String validarDataCriacao(LocalDate dataCriacao) {
        if (dataCriacao == null) {
            return "Data de criação inválida.";
        }
        if (dataCriacao.isAfter(LocalDate.now())) {
            return "Data de criação não pode ser futura.";
        }
        return null;
    }

    public BigDecimal ajustarDebitoBonus(BigDecimal bonus, BigDecimal valorDebito) {
        if (bonus == null || valorDebito == null) {
            return valorDebito;
        }
        BigDecimal valorComDesconto = valorDebito.subtract(bonus);
        if (valorComDesconto.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return valorComDesconto;
    }
}
