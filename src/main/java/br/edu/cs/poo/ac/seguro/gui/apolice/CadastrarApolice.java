package br.edu.cs.poo.ac.seguro.gui.apolice;

import br.edu.cs.poo.ac.seguro.entidades.CategoriaVeiculo;
import br.edu.cs.poo.ac.seguro.mediators.ApoliceMediator;
import br.edu.cs.poo.ac.seguro.mediators.DadosVeiculo;
import br.edu.cs.poo.ac.seguro.mediators.RetornoInclusaoApolice;

import javax.swing.*;
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

public class CadastrarApolice extends JFrame {

    private ApoliceMediator apoliceMediator;
    private JTextField txtCpfOuCnpj;
    private JTextField txtPlaca;
    private JFormattedTextField txtAno;
    private JFormattedTextField txtValorMaximoSegurado;

    private JComboBox<String> cmbCategoriaVeiculo;
    private List<CategoriaVeiculo> categoriasOrdenadas;

    private JButton btnIncluir;
    private JButton btnLimpar;

    public CadastrarApolice() {
        apoliceMediator = ApoliceMediator.getInstancia();

        setTitle("Inclusão de Apólice");
        setSize(500, 400); // Adjusted size back
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- CPF ou CNPJ ---
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("CPF/CNPJ:"), gbc);
        txtCpfOuCnpj = new JTextField(15);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(txtCpfOuCnpj, gbc);

        // --- Placa ---
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        add(new JLabel("Placa:"), gbc);
        txtPlaca = new JTextField(10);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(txtPlaca, gbc);

        // --- Ano do Veículo ---
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        add(new JLabel("Ano do Veículo:"), gbc);

        DecimalFormat anoDecimalFormat = new DecimalFormat("####");
        anoDecimalFormat.setGroupingUsed(false);
        anoDecimalFormat.setParseIntegerOnly(true);

        NumberFormatter anoFormatter = new NumberFormatter(anoDecimalFormat);
        anoFormatter.setValueClass(Integer.class);
        anoFormatter.setAllowsInvalid(true);
        anoFormatter.setMinimum(1900);
        anoFormatter.setMaximum(LocalDate.now().getYear() + 1);
        txtAno = new JFormattedTextField(anoFormatter);
        txtAno.setColumns(4);
        txtAno.setValue(null);
        txtAno.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(txtAno, gbc);

        // --- Valor Máximo Segurado ---
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.0;
        add(new JLabel("Valor Máximo Segurado:"), gbc);
        DecimalFormat valorDecimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        valorDecimalFormat.setParseBigDecimal(true);
        valorDecimalFormat.setMinimumFractionDigits(2);
        valorDecimalFormat.setMaximumFractionDigits(2);
        valorDecimalFormat.applyPattern("#,##0.00"); // Standard number pattern for pt-BR

        NumberFormatter valorFormatter = new NumberFormatter(valorDecimalFormat);
        valorFormatter.setValueClass(BigDecimal.class);
        valorFormatter.setAllowsInvalid(true);
        valorFormatter.setOverwriteMode(false);
        txtValorMaximoSegurado = new JFormattedTextField(valorFormatter);
        txtValorMaximoSegurado.setColumns(10);
        txtValorMaximoSegurado.setValue(null);
        txtValorMaximoSegurado.setFocusLostBehavior(JFormattedTextField.COMMIT);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(txtValorMaximoSegurado, gbc);

        // --- Categoria do Veículo ---
        gbc.gridx = 0; gbc.gridy = 4; // Adjusted row index
        gbc.weightx = 0.0;
        add(new JLabel("Categoria do Veículo:"), gbc);
        cmbCategoriaVeiculo = new JComboBox<>();
        popularCategorias();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(cmbCategoriaVeiculo, gbc);

        // --- Buttons Panel ---
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnIncluir = new JButton("Incluir");
        panelBotoes.add(btnIncluir);
        btnLimpar = new JButton("Limpar");
        panelBotoes.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 5; // Adjusted row index
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(panelBotoes, gbc);

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
            // --- Ano ---
            if (txtAno.getText().trim().isEmpty() || txtAno.getText().contains("_")) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Ano do Veículo.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                txtAno.requestFocusInWindow();
                return;
            }
            txtAno.commitEdit();
            Integer ano = (Integer) txtAno.getValue();
            if (ano == null) {
                JOptionPane.showMessageDialog(this, "Valor inválido para o Ano do Veículo. Por favor, insira um número inteiro válido (e.g., 2024).", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                txtAno.requestFocusInWindow();
                return;
            }

            // --- Valor Máximo Segurado ---
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

            // --- Categoria Veículo ---
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

            // --- Create DadosVeiculo and call mediator ---
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
                CadastrarApolice window = new CadastrarApolice();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}