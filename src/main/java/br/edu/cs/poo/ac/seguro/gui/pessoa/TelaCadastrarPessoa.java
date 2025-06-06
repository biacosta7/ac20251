package br.edu.cs.poo.ac.seguro.gui.pessoa;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import br.edu.cs.poo.ac.seguro.entidades.*;
import br.edu.cs.poo.ac.seguro.mediators.SeguradoPessoaMediator;

public class TelaCadastrarPessoa extends JFrame {
    private JTextField tfLogradouro, tfCep, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade, tfNome, tfData, tfBonus, tfCpf, tfRenda;

    public TelaCadastrarPessoa() {
        setTitle("Cadastrar Segurado Pessoa");
        setSize(400, 600);
        setLayout(null);
        setLocationRelativeTo(null);

        tfLogradouro = new JTextField(); tfCep = new JTextField(); tfNumero = new JTextField();
        tfComplemento = new JTextField(); tfPais = new JTextField(); tfEstado = new JTextField();
        tfCidade = new JTextField(); tfNome = new JTextField(); tfData = new JTextField();
        tfBonus = new JTextField(); tfCpf = new JTextField(); tfRenda = new JTextField();

        JLabel[] labels = {
                new JLabel("Logradouro:"), new JLabel("Cep:"), new JLabel("Número:"),
                new JLabel("Complemento:"), new JLabel("País"), new JLabel("Estado:"),
                new JLabel("Cidade:"), new JLabel("Nome:"), new JLabel("Data de Nascimento (YYYY-MM-DD):"),
                new JLabel("Bônus:"), new JLabel("CPF:"), new JLabel("Renda:")
        };

        JTextField[] fields = {
                tfLogradouro, tfCep, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade, tfNome, tfData, tfBonus, tfCpf, tfRenda
        };

        int y = 10;
        for (int i = 0; i < labels.length; i++) {
            labels[i].setBounds(20, y, 180, 20);
            fields[i].setBounds(200, y, 160, 25);
            add(labels[i]); add(fields[i]);
            y += 35;
        }

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(150, y, 100, 30);
        add(btnSalvar);

        btnSalvar.addActionListener(e -> cadastrarPessoa());
    }

    private void cadastrarPessoa() {
        try {
            String nome = tfNome.getText();
            Endereco endereco = new Endereco(
                    tfLogradouro.getText(),
                    tfCep.getText(),
                    tfNumero.getText(),
                    tfComplemento.getText(),
                    tfPais.getText(),
                    tfEstado.getText(),
                    tfCidade.getText()
            );
            LocalDate data = LocalDate.parse(tfData.getText());
            BigDecimal bonus = new BigDecimal(tfBonus.getText());
            String cpf = tfCpf.getText();
            double renda = Double.parseDouble(tfRenda.getText());

            SeguradoPessoa seg = new SeguradoPessoa(nome, endereco, data, bonus, cpf, renda);
            String erro = SeguradoPessoaMediator.getInstancia().incluirSeguradoPessoa(seg);

            if (erro == null) {
                JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dados inválidos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

