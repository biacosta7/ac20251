package br.edu.cs.poo.ac.seguro.gui.apolice;

import br.edu.cs.poo.ac.seguro.entidades.CategoriaVeiculo;
import br.edu.cs.poo.ac.seguro.mediators.ApoliceMediator;
import br.edu.cs.poo.ac.seguro.mediators.DadosVeiculo;
import br.edu.cs.poo.ac.seguro.mediators.RetornoInclusaoApolice;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class TelaCadastrarApolice extends JFrame {
    private JTextField tfCpfOuCnpj, tfPlaca;
    private JFormattedTextField tfAno, tfValorMaximoSegurado, tfDataInicioVigencia;

    private JComboBox<CategoriaVeiculo> comboBoxCategoria;
    private ApoliceMediator apoliceMediator;

    public TelaCadastrarApolice() {
        apoliceMediator = ApoliceMediator.getInstancia();

        setTitle("Inclusão de Apólice");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfCpfOuCnpj = new JTextField(15);
        tfPlaca = new JTextField(10);

        DecimalFormat anoDecimalFormat = new DecimalFormat("####");
        anoDecimalFormat.setGroupingUsed(false);
        anoDecimalFormat.setParseIntegerOnly(true);

        NumberFormatter anoFormatter = new NumberFormatter(anoDecimalFormat);
        anoFormatter.setValueClass(Integer.class);
        anoFormatter.setAllowsInvalid(true);
        anoFormatter.setMinimum(1900);
        anoFormatter.setMaximum(LocalDate.now().getYear() + 1);
        tfAno = new JFormattedTextField(anoFormatter);
        tfAno.setColumns(4);
        tfAno.setValue(null);
        tfAno.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

        DecimalFormat valorDecimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        valorDecimalFormat.setParseBigDecimal(true);
        valorDecimalFormat.setMinimumFractionDigits(2);
        valorDecimalFormat.setMaximumFractionDigits(2);
        valorDecimalFormat.applyPattern("R$ #,##0.00");
        valorDecimalFormat.applyPattern("#,##0.00");

        NumberFormatter valorFormatter = new NumberFormatter(valorDecimalFormat);
        valorFormatter.setValueClass(BigDecimal.class);
        valorFormatter.setAllowsInvalid(true);
        valorFormatter.setOverwriteMode(false);
        tfValorMaximoSegurado = new JFormattedTextField(valorFormatter);
        tfValorMaximoSegurado.setColumns(10);
        tfValorMaximoSegurado.setValue(null);
        tfValorMaximoSegurado.setFocusLostBehavior(JFormattedTextField.COMMIT);

        try {
            MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
            dateFormatter.setPlaceholderCharacter('_');
            tfDataInicioVigencia = new JFormattedTextField(dateFormatter);
            tfDataInicioVigencia.setColumns(10);
            tfDataInicioVigencia.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        } catch (ParseException e) {
            e.printStackTrace();
            tfDataInicioVigencia = new JFormattedTextField();
        }
        CategoriaVeiculo[] sortedCategories = getSortedCategories();
        comboBoxCategoria = new JComboBox<>(sortedCategories);
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("CPF/CNPJ:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        add(tfCpfOuCnpj, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Placa:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        add(tfPlaca, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Ano do Veículo:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        add(tfAno, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Valor Máximo Segurado:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        add(tfValorMaximoSegurado, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Data Início Vigência (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        add(tfDataInicioVigencia, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Categoria do Veículo:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        add(comboBoxCategoria, gbc);
        row++;

        JButton btnIncluir = new JButton("Incluir");
        JButton btnLimpar = new JButton("Limpar");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(btnIncluir);
        buttonPanel.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        btnIncluir.addActionListener(e -> incluirApolice());
        btnLimpar.addActionListener(e -> limparCampos());
    }

    private CategoriaVeiculo[] getSortedCategories() {
        CategoriaVeiculo[] categories = CategoriaVeiculo.values();
        Arrays.sort(categories, Comparator.comparing(CategoriaVeiculo::getNome));
        return categories;
    }

    private void incluirApolice() {
        String cpfOuCnpj = tfCpfOuCnpj.getText().trim();
        String placa = tfPlaca.getText().trim();

        if (cpfOuCnpj.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o campo CPF/CNPJ.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            tfCpfOuCnpj.requestFocusInWindow();
            return;
        }
        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Placa.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            tfPlaca.requestFocusInWindow();
            return;
        }

        try {
            if (tfAno.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Ano do Veículo.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfAno.requestFocusInWindow();
                return;
            }
            tfAno.commitEdit();
            Integer ano = (Integer) tfAno.getValue();
            if (ano == null) {
                JOptionPane.showMessageDialog(this, "Valor inválido para o Ano do Veículo. Por favor, insira um número inteiro válido (e.g., 2024).", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfAno.requestFocusInWindow();
                return;
            }


            if (tfValorMaximoSegurado.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Valor Máximo Segurado.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfValorMaximoSegurado.requestFocusInWindow();
                return;
            }
            tfValorMaximoSegurado.commitEdit();
            BigDecimal valorMaximoSegurado = (BigDecimal) tfValorMaximoSegurado.getValue();
            if (valorMaximoSegurado == null) { // This check is mostly for robustness after commitEdit
                JOptionPane.showMessageDialog(this, "Valor inválido para o Valor Máximo Segurado. Por favor, insira um valor monetário válido (e.g., 2000,00).", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfValorMaximoSegurado.requestFocusInWindow();
                return;
            }


            String dataStr = tfDataInicioVigencia.getText().replace("_", "").trim();
            if (dataStr.length() != 10) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha a Data de Início da Vigência no formato ####-##-##.", "Erro de Formato de Data", JOptionPane.WARNING_MESSAGE);
                tfDataInicioVigencia.requestFocusInWindow();
                return;
            }

            CategoriaVeiculo categoriaSelecionada = (CategoriaVeiculo) comboBoxCategoria.getSelectedItem();
            if (categoriaSelecionada == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione uma categoria de veículo.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                comboBoxCategoria.requestFocusInWindow();
                return;
            }

            DadosVeiculo dadosVeiculo = new DadosVeiculo(
                    cpfOuCnpj,
                    placa,
                    ano,
                    valorMaximoSegurado,
                    categoriaSelecionada.getCodigo()
            );

            RetornoInclusaoApolice retorno = apoliceMediator.incluirApolice(dadosVeiculo);

            if (retorno.getNumeroApolice() != null) {
                JOptionPane.showMessageDialog(this,
                        "Apólice incluída com sucesso! Anote o número da apólice: " + retorno.getNumeroApolice(),
                        "Inclusão Bem Sucedida", JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
            } else {
                JOptionPane.showMessageDialog(this,
                        retorno.getMensagemErro(),
                        "Problema de Validação", JOptionPane.WARNING_MESSAGE);
            }

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar campo numérico ou de data. Verifique se os valores estão corretos. Detalhe: " + ex.getMessage(), "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado ao cadastrar a apólice: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void limparCampos() {
        tfCpfOuCnpj.setText("");
        tfPlaca.setText("");
        tfAno.setValue(null);
        tfValorMaximoSegurado.setValue(null);
        tfDataInicioVigencia.setText("");
        if (comboBoxCategoria.getItemCount() > 0) {
            comboBoxCategoria.setSelectedIndex(0);
        }
        tfCpfOuCnpj.requestFocusInWindow();
    }
}