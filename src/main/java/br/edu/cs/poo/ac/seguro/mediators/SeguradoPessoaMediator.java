package br.edu.cs.poo.ac.seguro.mediators;

import br.edu.cs.poo.ac.seguro.daos.SeguradoPessoaDAO;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa;

import java.time.LocalDate;

import static br.edu.cs.poo.ac.seguro.mediators.StringUtils.ehNuloOuBranco;
import static br.edu.cs.poo.ac.seguro.mediators.StringUtils.temSomenteNumeros;
import static br.edu.cs.poo.ac.seguro.mediators.ValidadorCpfCnpj.ehCpfValido;

public class SeguradoPessoaMediator {
    private static SeguradoMediator seguradoMediator = SeguradoMediator.getInstancia();
    private SeguradoPessoaDAO dao = new SeguradoPessoaDAO();
    private static SeguradoPessoaMediator instancia = new SeguradoPessoaMediator();

    private SeguradoPessoaMediator() {
    }

    public static SeguradoPessoaMediator getInstancia() {
        return instancia;
    }

    public boolean calcularCpfValido(String cpf){
        if (cpf.equals("00000000000") || cpf.equals("11111111111") || cpf.equals("22222222222") ||
                cpf.equals("33333333333") || cpf.equals("44444444444") || cpf.equals("55555555555")
                || cpf.equals("66666666666") || cpf.equals("77777777777") || cpf.equals("88888888888")
                || cpf.equals("99999999999")){
            return(false);
        }

        int x = 10, i=0, soma=0;

        while(i<9 && x>=2){
            soma += (cpf.charAt(i) - '0') * x;
            i++;
            x--;
        }

        int resto = soma%11;
        int verificador;

        if(resto == 0 || resto == 1){
            verificador = 0;
        } else {
            verificador = 11 - resto;
        }
        if (verificador != (cpf.charAt(9) - '0')) {
            return false;
        }

        // 2º digito
        x = 11;
        i=0;
        soma=0;
        while(i<10 && x>=2){
            soma += (cpf.charAt(i) - '0') * x;
            i++;
            x--;
        }

        resto = soma%11;
        int segundoDigito;
        if(resto == 0 || resto == 1){
            segundoDigito = 0;
        } else {
            segundoDigito = 11 - resto;
        }

        if (segundoDigito != (cpf.charAt(10) - '0')) {
            return false;
        }

        return true;
    }

    public String validarCpf(String cpf) {

        if (ehNuloOuBranco(cpf)) {
            return "CPF deve ser informado";
        }

        if(cpf.length() != 11){
            return "CPF deve ter 11 caracteres";
        }

        if(!temSomenteNumeros(cpf) || !calcularCpfValido(cpf)){
            return "CPF com dígito inválido";
        }

        if (!ehCpfValido(cpf)) {
            return "CPF inválido";
        }

        return null;
    }

    public String validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            return "Data do nascimento deve ser informada";
        }

        return null;
    }


    public String validarRenda(double renda) {
        if (renda < 0) {
            return "Renda deve ser maior ou igual à zero";
        }
        return null;
    }

    public String incluirSeguradoPessoa(SeguradoPessoa seg) {
        String msg = validarSeguradoPessoa(seg);

        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        if (dao.buscar(seg.getCpf()) != null) {
            return "CPF do segurado pessoa já existente";
        }

        boolean ret = dao.incluir(seg);
        if (!ret) {
            return "Erro ao incluir segurado.";
        }
        return null;
    }

    public String alterarSeguradoPessoa(SeguradoPessoa seg) {
        String msg = validarSeguradoPessoa(seg);
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        if (dao.buscar(seg.getCpf()) == null) {
            return "CPF do segurado pessoa não existente";
        }
        boolean ret = dao.alterar(seg);
        if (!ret) {
            return "Erro ao alterar segurado pessoa.";
        }
        return null;
    }

    public String excluirSeguradoPessoa(String cpf) {
        if (dao.buscar(cpf) == null) {
            return "CPF do segurado pessoa não existente";
        }

        if (validarCpf(cpf) != null) {
            return "CPF inválido para exclusão.";
        }

        boolean ret = dao.excluir(cpf);
        if (!ret) {
            return "Erro ao excluir segurado pessoa.";
        }
        return null;
    }
    public SeguradoPessoa buscarSeguradoPessoa(String cpf) {
        if (validarCpf(cpf) != null) {
            return null;
        }
        return dao.buscar(cpf);
    }
    public String validarSeguradoPessoa(SeguradoPessoa seg) {
        if (seg == null) {
            return "Segurado inválido.";
        }
        String msg = seguradoMediator.validarNome(seg.getNome());
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        msg = seguradoMediator.validarEndereco(seg.getEndereco());
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        msg = validarDataNascimento(seg.getDataNascimento());
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        msg = validarCpf(seg.getCpf());
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        msg = validarRenda(seg.getRenda());
        if (!ehNuloOuBranco(msg)) {
            return msg;
        }
        return null;
    }
}