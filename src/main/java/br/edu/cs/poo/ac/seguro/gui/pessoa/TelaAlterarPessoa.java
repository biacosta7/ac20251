package br.edu.cs.poo.ac.seguro.gui.pessoa;

import br.edu.cs.poo.ac.seguro.entidades.*;
import br.edu.cs.poo.ac.seguro.mediators.SeguradoPessoaMediator;

import javax.swing.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TelaAlterarPessoa extends JFrame {
    private JTextField tfCpfBusca, tfNome, tfLogradouro, tfCep, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade, tfData, tfBonus, tfRenda;
    private JButton btnBuscar, btnSalvar;

    public TelaAlterarPessoa() {
        setTitle("Alterar Segurado Pessoa");
        setSize(420, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lblCnpjBusca = new JLabel("Digite o CPF:");
        lblCnpjBusca.setBounds(20, 10, 120, 25);
        tfCpfBusca = new JTextField();
        tfCpfBusca.setBounds(150, 10, 160, 25);
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(320, 10, 80, 25);

        add(lblCnpjBusca);
        add(tfCpfBusca);
        add(btnBuscar);

        // Campos de edição
        tfNome = new JTextField(); tfLogradouro = new JTextField(); tfCep = new JTextField(); tfNumero = new JTextField();
        tfComplemento = new JTextField(); tfPais = new JTextField(); tfEstado = new JTextField(); tfCidade = new JTextField();
        tfData = new JTextField(); tfBonus = new JTextField(); tfRenda = new JTextField();

        JLabel[] labels = {
                new JLabel("Nome:"), new JLabel("Logradouro:"), new JLabel("CEP:"), new JLabel("Número:"),
                new JLabel("Complemento:"), new JLabel("País:"), new JLabel("Estado:"), new JLabel("Cidade:"),
                new JLabel("Data de Nascimento (YYYY-MM-DD):"), new JLabel("Bônus:"), new JLabel("Renda:")
        };

        JTextField[] fields = {
                tfNome, tfLogradouro, tfCep, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade, tfData, tfBonus, tfRenda
        };

        int y = 50;
        for (int i = 0; i < labels.length; i++) {
            labels[i].setBounds(20, y, 180, 20);
            fields[i].setBounds(200, y, 160, 25);
            add(labels[i]); add(fields[i]);
            y += 35;
        }

        btnSalvar = new JButton("Salvar Alterações");
        btnSalvar.setBounds(120, y, 160, 30);
        add(btnSalvar);

        // Ações
        btnBuscar.addActionListener(e -> buscarPessoa());
        btnSalvar.addActionListener(e -> salvarAlteracoes());
    }

    private void buscarPessoa() {
        String cnpj = tfCpfBusca.getText();
        SeguradoPessoa seg = SeguradoPessoaMediator.getInstancia().buscarSeguradoPessoa(cnpj);

        if (seg == null) {
            JOptionPane.showMessageDialog(this, "Pessoa não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tfNome.setText(seg.getNome());
        tfLogradouro.setText(seg.getEndereco().getLogradouro());
        tfCep.setText(seg.getEndereco().getCep());
        tfNumero.setText(seg.getEndereco().getNumero());
        tfComplemento.setText(seg.getEndereco().getComplemento());
        tfPais.setText(seg.getEndereco().getPais());
        tfEstado.setText(seg.getEndereco().getEstado());
        tfCidade.setText(seg.getEndereco().getCidade());
        tfData.setText(seg.getDataNascimento().toString());
        tfBonus.setText(seg.getBonus().toString());
        tfRenda.setText(String.valueOf(seg.getRenda()));
    }

    private void salvarAlteracoes() {
        try {
            String cnpj = tfCpfBusca.getText();
            String nome = tfNome.getText();
            Endereco endereco = new Endereco(
                    tfLogradouro.getText(), tfCep.getText(), tfNumero.getText(),
                    tfComplemento.getText(), tfPais.getText(), tfEstado.getText(), tfCidade.getText()
            );
            LocalDate data = LocalDate.parse(tfData.getText());
            BigDecimal bonus = new BigDecimal(tfBonus.getText());
            double renda = Double.parseDouble(tfRenda.getText());

            SeguradoPessoa novaPessoa = new SeguradoPessoa(nome, endereco, data, bonus, cnpj, renda);

            String erro = SeguradoPessoaMediator.getInstancia().alterarSeguradoPessoa(novaPessoa);
            if (erro == null) {
                JOptionPane.showMessageDialog(this, "Alterações salvas com sucesso!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
