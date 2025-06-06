package br.edu.cs.poo.ac.seguro.gui.empresa;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import br.edu.cs.poo.ac.seguro.entidades.*;
import br.edu.cs.poo.ac.seguro.mediators.SeguradoEmpresaMediator;

public class TelaCadastrarEmpresa extends JFrame {
    private JTextField tfLogradouro, tfCep, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade, tfNome, tfData, tfBonus, tfCnpj, tfFaturamento;
    private JCheckBox cbLocadora;

    public TelaCadastrarEmpresa() {
        setTitle("Cadastrar Segurado Empresa");
        setSize(400, 600);
        setLayout(null);
        setLocationRelativeTo(null);

        tfLogradouro = new JTextField(); tfCep = new JTextField(); tfNumero = new JTextField();
        tfComplemento = new JTextField(); tfPais = new JTextField(); tfEstado = new JTextField();
        tfCidade = new JTextField(); tfNome = new JTextField(); tfData = new JTextField();
        tfBonus = new JTextField(); tfCnpj = new JTextField(); tfFaturamento = new JTextField();
        cbLocadora = new JCheckBox("É locadora");

        JLabel[] labels = {
                new JLabel("Logradouro:"), new JLabel("Cep:"), new JLabel("Número:"),
                new JLabel("Complemento:"), new JLabel("País"), new JLabel("Estado:"),
                new JLabel("Cidade:"), new JLabel("Nome:"), new JLabel("Data Abertura (YYYY-MM-DD):"),
                new JLabel("Bônus:"), new JLabel("CNPJ:"), new JLabel("Faturamento:")
        };

        JTextField[] fields = {
                tfLogradouro, tfCep, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade, tfNome, tfData, tfBonus, tfCnpj, tfFaturamento
        };

        int y = 10;
        for (int i = 0; i < labels.length; i++) {
            labels[i].setBounds(20, y, 180, 20);
            fields[i].setBounds(200, y, 160, 25);
            add(labels[i]); add(fields[i]);
            y += 35;
        }

        cbLocadora.setBounds(200, y, 160, 25);
        add(cbLocadora); y += 40;

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(150, y, 100, 30);
        add(btnSalvar);

        btnSalvar.addActionListener(e -> cadastrarEmpresa());
    }

    private void cadastrarEmpresa() {
        try {
            String nome = tfNome.getText();
            Endereco endereco = new Endereco(
                    tfLogradouro.getText(),         // logradouro
                    tfCep.getText(),         // cep
                    tfNumero.getText(),      // numero
                    tfComplemento.getText(), // complemento
                    tfPais.getText(),        // pais
                    tfEstado.getText(),      // estado
                    tfCidade.getText()       // cidade
            );
            LocalDate data = LocalDate.parse(tfData.getText());
            BigDecimal bonus = new BigDecimal(tfBonus.getText());
            String cnpj = tfCnpj.getText();
            double faturamento = Double.parseDouble(tfFaturamento.getText());
            boolean ehLocadora = cbLocadora.isSelected();

            SeguradoEmpresa seg = new SeguradoEmpresa(nome, endereco, data, bonus, cnpj, faturamento, ehLocadora);
            String erro = SeguradoEmpresaMediator.getInstancia().incluirSeguradoEmpresa(seg);

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

