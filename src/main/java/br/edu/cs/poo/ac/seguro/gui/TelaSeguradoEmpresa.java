package br.edu.cs.poo.ac.seguro.gui;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import br.edu.cs.poo.ac.seguro.entidades.Endereco;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;
import br.edu.cs.poo.ac.seguro.mediators.SeguradoEmpresaMediator;

public class TelaSeguradoEmpresa extends JFrame {

    private JTextField tfCnpj;
    private JTextField tfNome, tfBonus, tfLogradouro, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade;
    private JFormattedTextField tfData, tfFaturamento, tfCep;
    private JCheckBox cbLocadora;

    private JButton btnNovo;
    private JButton btnBuscar;
    private JButton btnIncluir;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JButton btnCancelar;

    private JTextField[] campos;

    public TelaSeguradoEmpresa() {
        setTitle("Cadastro de Segurado Empresa");
        setSize(600, 750); // Aumentei a largura e a altura
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblCnpj = new JLabel("CNPJ:");
        lblCnpj.setBounds(50, 20, 80, 25);
        add(lblCnpj);

        tfCnpj = new JTextField();
        tfCnpj.setBounds(130, 20, 150, 25);
        add(tfCnpj);

        btnNovo = new JButton("Novo");
        btnNovo.setBounds(300, 20, 80, 25);
        add(btnNovo);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(390, 20, 80, 25);
        add(btnBuscar);

        tfNome = new JTextField();
        tfBonus = new JTextField();
        cbLocadora = new JCheckBox("É locadora");

        // Formatador para data
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            tfData = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            tfData = new JFormattedTextField();
            e.printStackTrace();
        }

        // Formatador para faturamento
        NumberFormat faturamentoFormat = DecimalFormat.getNumberInstance();
        faturamentoFormat.setMinimumFractionDigits(2);
        faturamentoFormat.setMaximumFractionDigits(2);
        NumberFormatter faturamentoFormatter = new NumberFormatter(faturamentoFormat);
        faturamentoFormatter.setValueClass(Double.class);
        faturamentoFormatter.setAllowsInvalid(false);
        faturamentoFormatter.setCommitsOnValidEdit(true);
        tfFaturamento = new JFormattedTextField(faturamentoFormatter);
        tfFaturamento.setValue(0.00);

        // Campos de Endereço
        tfPais = new JTextField();
        tfEstado = new JTextField();
        tfCidade = new JTextField();
        try {
            MaskFormatter cepMask = new MaskFormatter("########"); // Apenas números, 8 dígitos
            tfCep = new JFormattedTextField(cepMask);
        } catch (ParseException e) {
            tfCep = new JFormattedTextField();
            e.printStackTrace();
        }
        tfLogradouro = new JTextField();
        tfNumero = new JTextField();
        tfComplemento = new JTextField();


        JLabel[] labels = {
                new JLabel("Nome:"),
                new JLabel("Data Abertura (DD/MM/YYYY):"),
                new JLabel("Faturamento:"),
                new JLabel("Bônus:"),
        };

        JTextField[] fields = {
                tfNome, tfData, tfFaturamento, tfBonus
        };

        int y = 60;
        int labelX = 50;
        int fieldX = 220;
        int labelWidth = 160;
        int fieldWidth = 250;
        int rowHeight = 30;
        int spacing = 5;

        for (int i = 0; i < labels.length; i++) {
            labels[i].setBounds(labelX, y, labelWidth, 25);
            fields[i].setBounds(fieldX, y, fieldWidth, 25);
            add(labels[i]);
            add(fields[i]);
            y += rowHeight + spacing;
        }

        cbLocadora.setBounds(fieldX, y, fieldWidth, 25);
        add(cbLocadora);
        y += rowHeight + spacing;

        JLabel lblEnderecoTitle = new JLabel("Endereço");
        lblEnderecoTitle.setBounds(labelX, y + 10, labelWidth + fieldWidth, 25);
        lblEnderecoTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblEnderecoTitle);
        y += rowHeight + 20;

        JLabel[] enderecoLabels = {
                new JLabel("País:"),
                new JLabel("Estado:"),
                new JLabel("Cidade:"),
                new JLabel("CEP:"),
                new JLabel("Logradouro:"),
                new JLabel("Número:"),
                new JLabel("Complemento:")
        };

        JTextField[] enderecoFields = {
                tfPais, tfEstado, tfCidade, tfCep, tfLogradouro, tfNumero, tfComplemento
        };

        for (int i = 0; i < enderecoLabels.length; i++) {
            enderecoLabels[i].setBounds(labelX, y, labelWidth, 25);
            enderecoFields[i].setBounds(fieldX, y, fieldWidth, 25);
            add(enderecoLabels[i]);
            add(enderecoFields[i]);
            y += rowHeight + spacing;
        }

        campos = new JTextField[]{
                tfNome, tfData, tfFaturamento, tfBonus, tfPais, tfEstado, tfCidade, tfCep, tfLogradouro, tfNumero, tfComplemento
        };


        btnIncluir = new JButton("Incluir");
        btnIncluir.setBounds(40, y + 20, 90, 30);
        add(btnIncluir);

        btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(140, y + 20, 90, 30);
        add(btnExcluir);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(240, y + 20, 90, 30);
        add(btnLimpar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(340, y + 20, 90, 30);
        add(btnCancelar);

        setCamposEditavel(false);
        cbLocadora.setEnabled(false);
        btnIncluir.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnLimpar.setEnabled(false);
        btnCancelar.setEnabled(false);

        btnNovo.addActionListener(e -> handleNovoAction());
        btnBuscar.addActionListener(e -> handleBuscarAction());
        btnIncluir.addActionListener(e -> incluirEmpresa());
        btnExcluir.addActionListener(e -> excluirEmpresa());
        btnLimpar.addActionListener(e -> limparCamposEResetarEstado());
        btnCancelar.addActionListener(e -> handleCancelarAction());
    }

    private void setCamposEditavel(boolean editable) {
        for (JTextField campo : campos) {
            campo.setEnabled(editable);
        }
        cbLocadora.setEnabled(editable);
    }

    private void limparCampos() {
        tfCnpj.setText("");
        for (JTextField campo : campos) {
            campo.setText("");
        }
        tfFaturamento.setValue(0.00);
        cbLocadora.setSelected(false);
    }

    private void limparCamposEResetarEstado() {
        limparCampos();
        tfCnpj.setEnabled(true);
        tfCnpj.setText("");
        btnNovo.setEnabled(true);
        btnBuscar.setEnabled(true);
        setCamposEditavel(false);
        btnIncluir.setText("Incluir");
        btnIncluir.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnLimpar.setEnabled(false);
        btnCancelar.setEnabled(false);
        tfCnpj.requestFocusInWindow();
    }

    private void handleNovoAction() {
        String cnpj = tfCnpj.getText().trim();
        String validacaoCnpj = SeguradoEmpresaMediator.getInstancia().validarCnpj(cnpj);

        if (validacaoCnpj != null) {
            JOptionPane.showMessageDialog(this, validacaoCnpj, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (SeguradoEmpresaMediator.getInstancia().buscarSeguradoEmpresa(cnpj) != null) {
            JOptionPane.showMessageDialog(this, "CNPJ já cadastrado. Use 'Buscar' para editar ou excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        limparCamposExceptCnpj();
        tfCnpj.setEnabled(false);
        setCamposEditavel(true);
        btnNovo.setEnabled(false);
        btnBuscar.setEnabled(false);
        btnIncluir.setText("Incluir");
        btnIncluir.setEnabled(true);
        btnExcluir.setEnabled(false);
        btnLimpar.setEnabled(true);
        btnCancelar.setEnabled(true);
        tfNome.requestFocusInWindow();
    }

    private void handleBuscarAction() {
        String cnpj = tfCnpj.getText().trim();
        String validacaoCnpj = SeguradoEmpresaMediator.getInstancia().validarCnpj(cnpj);

        if (validacaoCnpj != null) {
            JOptionPane.showMessageDialog(this, validacaoCnpj, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SeguradoEmpresa segurado = SeguradoEmpresaMediator.getInstancia().buscarSeguradoEmpresa(cnpj);

        if (segurado != null) {
            popularCampos(segurado);
            tfCnpj.setEnabled(false);
            setCamposEditavel(true);
            btnNovo.setEnabled(false);
            btnBuscar.setEnabled(false);
            btnIncluir.setText("Alterar");
            btnIncluir.setEnabled(true);
            btnExcluir.setEnabled(true);
            btnLimpar.setEnabled(true);
            btnCancelar.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Empresa encontrada e dados preenchidos.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            limparCamposExceptCnpj();
            tfCnpj.setText(cnpj);
            setCamposEditavel(false);
            btnIncluir.setEnabled(false);
            btnExcluir.setEnabled(false);
            btnLimpar.setEnabled(false);
            btnCancelar.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Nenhuma empresa encontrada com o CNPJ: " + cnpj + "\nUse 'Novo' para cadastrar.", "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void popularCampos(SeguradoEmpresa segurado) {
        tfNome.setText(segurado.getNome());
        Endereco endereco = segurado.getEndereco();
        if (endereco != null) {
            tfLogradouro.setText(endereco.getLogradouro());
            tfCep.setText(endereco.getCep());
            tfNumero.setText(endereco.getNumero());
            tfComplemento.setText(endereco.getComplemento());
            tfPais.setText(endereco.getPais());
            tfEstado.setText(endereco.getEstado());
            tfCidade.setText(endereco.getCidade());
        } else {
            tfLogradouro.setText("");
            tfCep.setText("");
            tfNumero.setText("");
            tfComplemento.setText("");
            tfPais.setText("");
            tfEstado.setText("");
            tfCidade.setText("");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        tfData.setText(segurado.getDataAbertura() != null ? segurado.getDataAbertura().format(formatter) : "");
        tfBonus.setText(segurado.getBonus() != null ? segurado.getBonus().toPlainString() : "");
        tfFaturamento.setValue(segurado.getFaturamento());
        cbLocadora.setSelected(segurado.isEhLocadoraDeVeiculos());
    }

    private void limparCamposExceptCnpj() {
        for (JTextField campo : campos) {
            campo.setText("");
        }
        tfFaturamento.setValue(0.00);
        cbLocadora.setSelected(false);
    }

    private void handleCancelarAction() {
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja descartar as alterações e retornar ao estado inicial?", "Cancelar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            limparCamposEResetarEstado();
        }
    }

    private void incluirEmpresa() {
        String cnpj = tfCnpj.getText().trim();

        try {
            String nome = tfNome.getText();
            Endereco endereco = new Endereco(
                    tfLogradouro.getText(),
                    tfCep.getText().replaceAll("[^0-9]", ""), // Remove caracteres não numéricos do CEP
                    tfNumero.getText(),
                    tfComplemento.getText(),
                    tfPais.getText(),
                    tfEstado.getText(),
                    tfCidade.getText()
            );

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate data = LocalDate.parse(tfData.getText(), formatter);

            BigDecimal bonus = new BigDecimal(tfBonus.getText());
            double faturamento = ((Number)tfFaturamento.getValue()).doubleValue();
            boolean ehLocadora = cbLocadora.isSelected();

            SeguradoEmpresa seg = new SeguradoEmpresa(nome, endereco, data, bonus, cnpj, faturamento, ehLocadora);

            String erro;
            if (btnIncluir.getText().equals("Incluir")) {
                erro = SeguradoEmpresaMediator.getInstancia().incluirSeguradoEmpresa(seg);
            } else {
                erro = SeguradoEmpresaMediator.getInstancia().alterarSeguradoEmpresa(seg);
            }

            if (erro == null) {
                JOptionPane.showMessageDialog(this, "Operação realizada com sucesso!");
                limparCamposEResetarEstado();
            } else {
                JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use DD/MM/YYYY.", "Erro de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bônus deve ser um número válido.", "Erro de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirEmpresa() {
        String cnpj = tfCnpj.getText().trim();
        String validacaoCnpj = SeguradoEmpresaMediator.getInstancia().validarCnpj(cnpj);

        if (validacaoCnpj != null) {
            JOptionPane.showMessageDialog(this, validacaoCnpj, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir a empresa com CNPJ: " + cnpj + "?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String erro = SeguradoEmpresaMediator.getInstancia().excluirSeguradoEmpresa(cnpj);
            if (erro == null) {
                JOptionPane.showMessageDialog(this, "Empresa excluída com sucesso.");
                limparCamposEResetarEstado();
            } else {
                JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaSeguradoEmpresa().setVisible(true));
    }
}