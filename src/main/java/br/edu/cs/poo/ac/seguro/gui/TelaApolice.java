package br.edu.cs.poo.ac.seguro.gui;

import br.edu.cs.poo.ac.seguro.entidades.CategoriaVeiculo;
import br.edu.cs.poo.ac.seguro.mediators.ApoliceMediator;
import br.edu.cs.poo.ac.seguro.mediators.DadosVeiculo;
import br.edu.cs.poo.ac.seguro.mediators.RetornoInclusaoApolice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TelaApolice extends JFrame {

    private ApoliceMediator apoliceMediator;
    private JTextField txtCpfOuCnpj;
    private JTextField txtPlaca;
    private JFormattedTextField txtAno;
    private JFormattedTextField txtValorMaximoSegurado;

    private JComboBox<String> cmbCategoriaVeiculo;
    private List<CategoriaVeiculo> categoriasOrdenadas;

    private JButton btnIncluir;
    private JButton btnLimpar;

    public TelaApolice() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        apoliceMediator = ApoliceMediator.getInstancia();

        setTitle("Inclusão de Apólice de Veículo");
        setSize(550, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCpfCnpj = new JLabel("CPF/CNPJ:");
        lblCpfCnpj.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(lblCpfCnpj, gbc);
        txtCpfOuCnpj = new JTextField(20);
        txtCpfOuCnpj.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(txtCpfOuCnpj, gbc);

        JLabel lblPlaca = new JLabel("Placa:");
        lblPlaca.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        mainPanel.add(lblPlaca, gbc);
        txtPlaca = new JTextField(15);
        txtPlaca.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(txtPlaca, gbc);

        JLabel lblAno = new JLabel("Ano do Veículo:");
        lblAno.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        mainPanel.add(lblAno, gbc);

        DecimalFormat anoDecimalFormat = new DecimalFormat("####");
        anoDecimalFormat.setGroupingUsed(false);
        anoDecimalFormat.setParseIntegerOnly(true);

        NumberFormatter anoFormatter = new NumberFormatter(anoDecimalFormat);
        anoFormatter.setValueClass(Integer.class);
        anoFormatter.setAllowsInvalid(true);
        anoFormatter.setMinimum(1900);
        anoFormatter.setMaximum(LocalDate.now().getYear() + 1);
        txtAno = new JFormattedTextField(anoFormatter);
        txtAno.setColumns(6);
        txtAno.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtAno.setValue(null);
        txtAno.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(txtAno, gbc);

        JLabel lblValorMaximo = new JLabel("Valor Máximo Segurado:");
        lblValorMaximo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.0;
        mainPanel.add(lblValorMaximo, gbc);
        DecimalFormat valorDecimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        valorDecimalFormat.setParseBigDecimal(true);
        valorDecimalFormat.setMinimumFractionDigits(2);
        valorDecimalFormat.setMaximumFractionDigits(2);
        valorDecimalFormat.applyPattern("#,##0.00");

        NumberFormatter valorFormatter = new NumberFormatter(valorDecimalFormat);
        valorFormatter.setValueClass(BigDecimal.class);
        valorFormatter.setAllowsInvalid(true);
        valorFormatter.setOverwriteMode(false);
        txtValorMaximoSegurado = new JFormattedTextField(valorFormatter);
        txtValorMaximoSegurado.setColumns(12);
        txtValorMaximoSegurado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtValorMaximoSegurado.setValue(null);
        txtValorMaximoSegurado.setFocusLostBehavior(JFormattedTextField.COMMIT);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(txtValorMaximoSegurado, gbc);

        JLabel lblCategoria = new JLabel("Categoria do Veículo:");
        lblCategoria.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 0.0;
        mainPanel.add(lblCategoria, gbc);
        cmbCategoriaVeiculo = new JComboBox<>();
        cmbCategoriaVeiculo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        popularCategorias();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(cmbCategoriaVeiculo, gbc);

        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        panelBotoes.setBackground(new Color(240, 248, 255));

        btnIncluir = new JButton("Incluir Apólice");
        btnIncluir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnIncluir.setBackground(new Color(60, 179, 113));
        btnIncluir.setForeground(Color.WHITE);
        btnIncluir.setFocusPainted(false);
        panelBotoes.add(btnIncluir);

        btnLimpar = new JButton("Limpar Campos");
        btnLimpar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLimpar.setBackground(new Color(100, 149, 237));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFocusPainted(false);
        panelBotoes.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(panelBotoes, gbc);

        add(mainPanel);

        addListeners();
    }

    private void popularCategorias() {
        CategoriaVeiculo[] todasCategorias = CategoriaVeiculo.values();
        categoriasOrdenadas = new ArrayList<>(Arrays.asList(todasCategorias));
        categoriasOrdenadas.sort(Comparator.comparing(CategoriaVeiculo::getNome));

        for (CategoriaVeiculo cat : categoriasOrdenadas) {
            cmbCategoriaVeiculo.addItem(cat.getNome());
        }
        if (!categoriasOrdenadas.isEmpty()) {
            cmbCategoriaVeiculo.setSelectedIndex(0);
        }
    }

    private void clearFields() {
        txtCpfOuCnpj.setText("");
        txtPlaca.setText("");
        txtAno.setValue(null);
        txtValorMaximoSegurado.setValue(null);
        if (cmbCategoriaVeiculo.getItemCount() > 0) {
            cmbCategoriaVeiculo.setSelectedIndex(0);
        }
        txtCpfOuCnpj.requestFocusInWindow();
    }

    private void addListeners() {
        btnIncluir.addActionListener(e -> incluirApolice());
        btnLimpar.addActionListener(e -> clearFields());
    }

    private void incluirApolice() {
        String cpfOuCnpj = txtCpfOuCnpj.getText().trim();
        String placa = txtPlaca.getText().trim();

        if (cpfOuCnpj.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o campo CPF/CNPJ.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            txtCpfOuCnpj.requestFocusInWindow();
            return;
        }
        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Placa.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            txtPlaca.requestFocusInWindow();
            return;
        }

        try {
            if (txtAno.getText().trim().isEmpty() || txtAno.getText().contains("_")) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Ano do Veículo.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                txtAno.requestFocusInWindow();
                return;
            }
            txtAno.commitEdit();
            Integer ano = (Integer) txtAno.getValue();
            if (ano == null) {
                JOptionPane.showMessageDialog(this, "Valor inválido para o Ano do Veículo. Por favor, insira um número inteiro válido (e.g., " + LocalDate.now().getYear() + ").", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                txtAno.requestFocusInWindow();
                return;
            }

            if (txtValorMaximoSegurado.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Valor Máximo Segurado.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                txtValorMaximoSegurado.requestFocusInWindow();
                return;
            }
            txtValorMaximoSegurado.commitEdit();
            BigDecimal valorMaximoSegurado = (BigDecimal) txtValorMaximoSegurado.getValue();
            if (valorMaximoSegurado == null) {
                JOptionPane.showMessageDialog(this, "Valor inválido para o Valor Máximo Segurado. Por favor, insira um valor monetário válido (e.g., 2000,00).", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                txtValorMaximoSegurado.requestFocusInWindow();
                return;
            }

            int selectedIndex = cmbCategoriaVeiculo.getSelectedIndex();
            CategoriaVeiculo categoriaSelecionada = null;
            if (selectedIndex >= 0 && selectedIndex < categoriasOrdenadas.size()) {
                categoriaSelecionada = categoriasOrdenadas.get(selectedIndex);
            }
            if (categoriaSelecionada == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione uma categoria de veículo.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                cmbCategoriaVeiculo.requestFocusInWindow();
                return;
            }
            int codigoCategoria = categoriaSelecionada.getCodigo();

            DadosVeiculo dadosVeiculo = new DadosVeiculo(
                    cpfOuCnpj,
                    placa,
                    ano,
                    valorMaximoSegurado,
                    codigoCategoria
            );

            RetornoInclusaoApolice retorno = apoliceMediator.incluirApolice(dadosVeiculo);

            if (retorno.getNumeroApolice() != null) {
                JOptionPane.showMessageDialog(this,
                        "Apólice incluída com sucesso! Anote o número da apólice: " + retorno.getNumeroApolice(),
                        "Inclusão Bem Sucedida", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        retorno.getMensagemErro(),
                        "Problema de Validação", JOptionPane.WARNING_MESSAGE);
            }

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar campo numérico. Verifique se os valores estão corretos. Detalhe: " + ex.getMessage(), "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado ao cadastrar a apólice: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                TelaApolice window = new TelaApolice();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}