package br.edu.cs.poo.ac.seguro.gui.empresa;

import br.edu.cs.poo.ac.seguro.entidades.*;
import br.edu.cs.poo.ac.seguro.mediators.SeguradoEmpresaMediator;

import javax.swing.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TelaAlterarEmpresa extends JFrame {
    private JTextField tfCnpjBusca, tfNome, tfLogradouro, tfCep, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade, tfData, tfBonus, tfFaturamento;
    private JCheckBox cbLocadora;
    private JButton btnBuscar, btnSalvar;

    public TelaAlterarEmpresa() {
        setTitle("Alterar Segurado Empresa");
        setSize(420, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lblCnpjBusca = new JLabel("Digite o CNPJ:");
        lblCnpjBusca.setBounds(20, 10, 120, 25);
        tfCnpjBusca = new JTextField();
        tfCnpjBusca.setBounds(150, 10, 160, 25);
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(320, 10, 80, 25);

        add(lblCnpjBusca);
        add(tfCnpjBusca);
        add(btnBuscar);

        // Campos de edição
        tfNome = new JTextField(); tfLogradouro = new JTextField(); tfCep = new JTextField(); tfNumero = new JTextField();
        tfComplemento = new JTextField(); tfPais = new JTextField(); tfEstado = new JTextField(); tfCidade = new JTextField();
        tfData = new JTextField(); tfBonus = new JTextField(); tfFaturamento = new JTextField();
        cbLocadora = new JCheckBox("É locadora");

        JLabel[] labels = {
                new JLabel("Nome:"), new JLabel("Logradouro:"), new JLabel("CEP:"), new JLabel("Número:"),
                new JLabel("Complemento:"), new JLabel("País:"), new JLabel("Estado:"), new JLabel("Cidade:"),
                new JLabel("Data Abertura (YYYY-MM-DD):"), new JLabel("Bônus:"), new JLabel("Faturamento:")
        };

        JTextField[] fields = {
                tfNome, tfLogradouro, tfCep, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade, tfData, tfBonus, tfFaturamento
        };

        int y = 50;
        for (int i = 0; i < labels.length; i++) {
            labels[i].setBounds(20, y, 180, 20);
            fields[i].setBounds(200, y, 160, 25);
            add(labels[i]); add(fields[i]);
            y += 35;
        }

        cbLocadora.setBounds(200, y, 160, 25);
        add(cbLocadora);
        y += 40;

        btnSalvar = new JButton("Salvar Alterações");
        btnSalvar.setBounds(120, y, 160, 30);
        add(btnSalvar);

        // Ações
        btnBuscar.addActionListener(e -> buscarEmpresa());
        btnSalvar.addActionListener(e -> salvarAlteracoes());
    }

    private void buscarEmpresa() {
        String cnpj = tfCnpjBusca.getText();
        SeguradoEmpresa seg = SeguradoEmpresaMediator.getInstancia().buscarSeguradoEmpresa(cnpj);

        if (seg == null) {
            JOptionPane.showMessageDialog(this, "Empresa não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE);
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
        tfData.setText(seg.getDataAbertura().toString());
        tfBonus.setText(seg.getBonus().toString());
        tfFaturamento.setText(String.valueOf(seg.getFaturamento()));
        cbLocadora.setSelected(seg.isEhLocadoraDeVeiculos());
    }

    private void salvarAlteracoes() {
        try {
            String cnpj = tfCnpjBusca.getText(); // mantém o CNPJ original
            String nome = tfNome.getText();
            Endereco endereco = new Endereco(
                    tfLogradouro.getText(), tfCep.getText(), tfNumero.getText(),
                    tfComplemento.getText(), tfPais.getText(), tfEstado.getText(), tfCidade.getText()
            );
            LocalDate data = LocalDate.parse(tfData.getText());
            BigDecimal bonus = new BigDecimal(tfBonus.getText());
            double faturamento = Double.parseDouble(tfFaturamento.getText());
            boolean ehLocadora = cbLocadora.isSelected();

            SeguradoEmpresa novaEmpresa = new SeguradoEmpresa(nome, endereco, data, bonus, cnpj, faturamento, ehLocadora);

            String erro = SeguradoEmpresaMediator.getInstancia().alterarSeguradoEmpresa(novaEmpresa);
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
