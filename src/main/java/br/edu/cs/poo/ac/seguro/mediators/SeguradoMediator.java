package br.edu.cs.poo.ac.seguro.mediators;

import br.edu.cs.poo.ac.seguro.entidades.Endereco;
import java.math.BigDecimal;
import java.time.LocalDate;

import static br.edu.cs.poo.ac.seguro.mediators.StringUtils.ehNuloOuBranco;
import static br.edu.cs.poo.ac.seguro.mediators.StringUtils.temSomenteNumeros;

public class SeguradoMediator {
    private static SeguradoMediator instancia = new SeguradoMediator();
    private SeguradoMediator() {
    }
    public static SeguradoMediator getInstancia() {
        return instancia;
    }

    public String validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty())
            return "Nome deve ser informado";
        else if(nome.length() > 100)
            return "Tamanho do nome deve ser no máximo 100 caracteres";

        return null;
    }

    public String validarEndereco(Endereco endereco) {
        if(endereco == null)
            return "Endereço deve ser informado";
        else if (endereco.getCep() == null || endereco.getCep().isBlank())
            return "CEP deve ser informado";
        else if (endereco.getCep().length() != 8)
            return "Tamanho do CEP deve ser 8 caracteres";
        else if(!temSomenteNumeros(endereco.getCep()))
            return "CEP deve ter formato NNNNNNNN";

        else if (endereco.getLogradouro() == null || endereco.getLogradouro().isBlank())
            return "Logradouro deve ser informado";
        else if (endereco.getCidade() == null || endereco.getCidade().isBlank())
            return "Cidade deve ser informada";
        else if (endereco.getCidade().length() > 100)
            return "Tamanho da cidade deve ser no máximo 100 caracteres";

        else if (endereco.getEstado() == null || endereco.getEstado().isBlank())
            return "Sigla do estado deve ser informada";
        else if (endereco.getEstado().length() != 2)
            return "Tamanho da sigla do estado deve ser 2 caracteres";

        else if(endereco.getPais() == null || endereco.getPais().isBlank() || endereco.getPais().isEmpty())
            return "País deve ser informado";
        else if(endereco.getPais().length() > 40)
            return "Tamanho do país deve ser no máximo 40 caracteres";

        else if (endereco.getNumero() != null && endereco.getNumero().length() > 20)
            return "Tamanho do número deve ser no máximo 20 caracteres";

        else if(endereco.getComplemento() != null && endereco.getComplemento().length() > 30)
            return "Tamanho do complemento deve ser no máximo 30 caracteres";

        return null;
    }

    public String validarDataCriacao(LocalDate dataCriacao) {
        if (dataCriacao == null) {
            return "Data da criação deve ser informada";
        }
        else if (dataCriacao.isAfter(LocalDate.now())) {
            return "Data da criação deve ser menor ou igual à data atual";
        }
        return null;
    }

    public BigDecimal ajustarDebitoBonus(BigDecimal bonus, BigDecimal valorDebito) {
        if (bonus == null || valorDebito == null) {
            return BigDecimal.ZERO;
        }

        return bonus.min(valorDebito);
    }

}
