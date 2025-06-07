package br.edu.cs.poo.ac.seguro.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.edu.cs.poo.ac.seguro.entidades.Endereco;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;
import br.edu.cs.poo.ac.seguro.mediators.SeguradoEmpresaMediator;

public class TelaSeguradoEmpresa extends JFrame {

    private JTextField tfCnpj; // Regular JTextField for flexible pasting
    private JTextField tfNome, tfBonus;
    private JFormattedTextField tfData, tfFaturamento;
    private JTextField tfLogradouro, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade;
    private JFormattedTextField tfCep; // JFormattedTextField with MaskFormatter

    private JCheckBox cbLocadora;

    private JButton btnNovo;
    private JButton btnBuscar;
    private JButton btnIncluir;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JButton btnCancelar;

    // Use a List<JComponent> for more flexible enabling/disabling of all relevant input fields
    private List<JComponent> editableFields;

    public TelaSeguradoEmpresa() {
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

        setTitle("Cadastro de Segurado Empresa");
        setSize(700, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255));
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        editableFields = new ArrayList<>(); // Initialize the list

        // --- CNPJ and Search/New Buttons ---
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblCnpj = new JLabel("CNPJ:");
        lblCnpj.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(lblCnpj, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 0.5;
        tfCnpj = new JTextField(18); // Plain JTextField
        tfCnpj.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(tfCnpj, gbc);
        editableFields.add(tfCnpj); // Add CNPJ to the editable fields list

        JPanel cnpjButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        cnpjButtonsPanel.setBackground(new Color(240, 248, 255));

        btnNovo = new JButton("Novo");
        btnNovo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNovo.setBackground(new Color(95, 158, 160));
        btnNovo.setForeground(Color.WHITE);
        btnNovo.setFocusPainted(false);
        cnpjButtonsPanel.add(btnNovo);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuscar.setBackground(new Color(70, 130, 180));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        cnpjButtonsPanel.add(btnBuscar);

        gbc.gridx = 2; gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.gridwidth = 2;
        mainPanel.add(cnpjButtonsPanel, gbc);

        // --- Company Details Panel ---
        JPanel companyDetailsPanel = new JPanel(new GridBagLayout());
        companyDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Dados da Empresa", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(50, 50, 50)));
        companyDetailsPanel.setBackground(new Color(250, 255, 250));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        mainPanel.add(companyDetailsPanel, gbc);

        GridBagConstraints cdpGbc = new GridBagConstraints();
        cdpGbc.insets = new Insets(5, 10, 5, 10);
        cdpGbc.fill = GridBagConstraints.HORIZONTAL;
        cdpGbc.anchor = GridBagConstraints.WEST;

        // Name
        cdpGbc.gridx = 0; cdpGbc.gridy = 0;
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 12));
        companyDetailsPanel.add(lblNome, cdpGbc);
        cdpGbc.gridx = 1; cdpGbc.weightx = 1.0;
        tfNome = new JTextField();
        tfNome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        companyDetailsPanel.add(tfNome, cdpGbc);
        editableFields.add(tfNome);

        // Data Abertura
        cdpGbc.gridx = 0; cdpGbc.gridy = 1; cdpGbc.weightx = 0.0;
        JLabel lblData = new JLabel("Data Abertura (DD/MM/YYYY):");
        lblData.setFont(new Font("Segoe UI", Font.BOLD, 12));
        companyDetailsPanel.add(lblData, cdpGbc);
        cdpGbc.gridx = 1; cdpGbc.weightx = 1.0;
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            dateMask.setAllowsInvalid(true); // *** IMPORTANT: Allow invalid states for pasting ***
            dateMask.setOverwriteMode(false); // Allow insertion, not overwrite
            tfData = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            tfData = new JFormattedTextField();
            e.printStackTrace();
        }
        tfData.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tfData.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT); // Crucial for mask to apply on paste/input
        companyDetailsPanel.add(tfData, cdpGbc);
        editableFields.add(tfData);

        // Faturamento
        cdpGbc.gridx = 0; cdpGbc.gridy = 2; cdpGbc.weightx = 0.0;
        JLabel lblFaturamento = new JLabel("Faturamento:");
        lblFaturamento.setFont(new Font("Segoe UI", Font.BOLD, 12));
        companyDetailsPanel.add(lblFaturamento, cdpGbc);
        cdpGbc.gridx = 1; cdpGbc.weightx = 1.0;
        DecimalFormat faturamentoFormat = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        faturamentoFormat.setMinimumFractionDigits(2);
        faturamentoFormat.setMaximumFractionDigits(2);
        faturamentoFormat.setParseBigDecimal(true); // Important for getting BigDecimal value
        faturamentoFormat.applyPattern("#,##0.00");
        NumberFormatter faturamentoFormatter = new NumberFormatter(faturamentoFormat);
        faturamentoFormatter.setValueClass(BigDecimal.class);
        faturamentoFormatter.setAllowsInvalid(true); // *** IMPORTANT: Allow invalid states for pasting/typing ***
        faturamentoFormatter.setOverwriteMode(false); // Allow insertion for pasting
        tfFaturamento = new JFormattedTextField(faturamentoFormatter);
        tfFaturamento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tfFaturamento.setValue(BigDecimal.ZERO);
        tfFaturamento.setFocusLostBehavior(JFormattedTextField.COMMIT); // Commit on focus lost
        companyDetailsPanel.add(tfFaturamento, cdpGbc);
        editableFields.add(tfFaturamento);

        // Bonus
        cdpGbc.gridx = 0; cdpGbc.gridy = 3; cdpGbc.weightx = 0.0;
        JLabel lblBonus = new JLabel("Bônus:");
        lblBonus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        companyDetailsPanel.add(lblBonus, cdpGbc);
        cdpGbc.gridx = 1; cdpGbc.weightx = 1.0;
        tfBonus = new JTextField(); // Changed to JTextField for flexible bonus input/pasting
        tfBonus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        companyDetailsPanel.add(tfBonus, cdpGbc);
        editableFields.add(tfBonus);

        // Is Locadora CheckBox
        cdpGbc.gridx = 1; cdpGbc.gridy = 4;
        cdpGbc.weightx = 1.0;
        cdpGbc.anchor = GridBagConstraints.WEST;
        cbLocadora = new JCheckBox("É locadora de veículos");
        cbLocadora.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbLocadora.setBackground(new Color(250, 255, 250));
        companyDetailsPanel.add(cbLocadora, cdpGbc);
        editableFields.add(cbLocadora);

        // --- Address Details Panel ---
        JPanel addressDetailsPanel = new JPanel(new GridBagLayout());
        addressDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Endereço", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(50, 50, 50)));
        addressDetailsPanel.setBackground(new Color(250, 250, 255));
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        mainPanel.add(addressDetailsPanel, gbc);

        GridBagConstraints adpGbc = new GridBagConstraints();
        adpGbc.insets = new Insets(5, 10, 5, 10);
        adpGbc.fill = GridBagConstraints.HORIZONTAL;
        adpGbc.anchor = GridBagConstraints.WEST;

        // País
        adpGbc.gridx = 0; adpGbc.gridy = 0;
        JLabel lblPais = new JLabel("País:");
        lblPais.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addressDetailsPanel.add(lblPais, adpGbc);
        adpGbc.gridx = 1; adpGbc.weightx = 1.0;
        tfPais = new JTextField();
        tfPais.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addressDetailsPanel.add(tfPais, adpGbc);
        editableFields.add(tfPais);

        // Estado
        adpGbc.gridx = 0; adpGbc.gridy = 1; adpGbc.weightx = 0.0;
        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addressDetailsPanel.add(lblEstado, adpGbc);
        adpGbc.gridx = 1; adpGbc.weightx = 1.0;
        tfEstado = new JTextField();
        tfEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addressDetailsPanel.add(tfEstado, adpGbc);
        editableFields.add(tfEstado);

        // Cidade
        adpGbc.gridx = 0; adpGbc.gridy = 2; adpGbc.weightx = 0.0;
        JLabel lblCidade = new JLabel("Cidade:");
        lblCidade.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addressDetailsPanel.add(lblCidade, adpGbc);
        adpGbc.gridx = 1; adpGbc.weightx = 1.0;
        tfCidade = new JTextField();
        tfCidade.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addressDetailsPanel.add(tfCidade, adpGbc);
        editableFields.add(tfCidade);

        // CEP
        adpGbc.gridx = 0; adpGbc.gridy = 3; adpGbc.weightx = 0.0;
        JLabel lblCep = new JLabel("CEP:");
        lblCep.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addressDetailsPanel.add(lblCep, adpGbc);
        adpGbc.gridx = 1; adpGbc.weightx = 1.0;
        try {
            MaskFormatter cepMask = new MaskFormatter("########");
            cepMask.setPlaceholderCharacter('_');
            cepMask.setAllowsInvalid(true); // *** IMPORTANT: Allow invalid states for pasting ***
            cepMask.setOverwriteMode(false); // Allow insertion, not overwrite
            tfCep = new JFormattedTextField(cepMask);
        } catch (ParseException e) {
            tfCep = new JFormattedTextField();
            e.printStackTrace();
        }
        tfCep.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tfCep.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT); // Crucial for mask to apply on paste
        addressDetailsPanel.add(tfCep, adpGbc);
        editableFields.add(tfCep);

        // Logradouro
        adpGbc.gridx = 0; adpGbc.gridy = 4; adpGbc.weightx = 0.0;
        JLabel lblLogradouro = new JLabel("Logradouro:");
        lblLogradouro.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addressDetailsPanel.add(lblLogradouro, adpGbc);
        adpGbc.gridx = 1; adpGbc.weightx = 1.0;
        tfLogradouro = new JTextField();
        tfLogradouro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addressDetailsPanel.add(tfLogradouro, adpGbc);
        editableFields.add(tfLogradouro);

        // Número
        adpGbc.gridx = 0; adpGbc.gridy = 5; adpGbc.weightx = 0.0;
        JLabel lblNumero = new JLabel("Número:");
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addressDetailsPanel.add(lblNumero, adpGbc);
        adpGbc.gridx = 1; adpGbc.weightx = 1.0;
        tfNumero = new JTextField();
        tfNumero.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addressDetailsPanel.add(tfNumero, adpGbc);
        editableFields.add(tfNumero);

        // Complemento
        adpGbc.gridx = 0; adpGbc.gridy = 6; adpGbc.weightx = 0.0;
        JLabel lblComplemento = new JLabel("Complemento:");
        lblComplemento.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addressDetailsPanel.add(lblComplemento, adpGbc);
        adpGbc.gridx = 1; adpGbc.weightx = 1.0;
        tfComplemento = new JTextField();
        tfComplemento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addressDetailsPanel.add(tfComplemento, adpGbc);
        editableFields.add(tfComplemento);


        // --- Action Buttons Panel ---
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        actionButtonsPanel.setBackground(new Color(240, 248, 255));

        btnIncluir = new JButton("Incluir");
        btnIncluir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnIncluir.setBackground(new Color(60, 179, 113));
        btnIncluir.setForeground(Color.WHITE);
        btnIncluir.setFocusPainted(false);
        actionButtonsPanel.add(btnIncluir);

        btnExcluir = new JButton("Excluir");
        btnExcluir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExcluir.setBackground(new Color(220, 20, 60));
        btnExcluir.setForeground(Color.WHITE);
        btnExcluir.setFocusPainted(false);
        actionButtonsPanel.add(btnExcluir);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLimpar.setBackground(new Color(100, 149, 237));
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFocusPainted(false);
        actionButtonsPanel.add(btnLimpar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.setBackground(new Color(255, 165, 0));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        actionButtonsPanel.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(actionButtonsPanel, gbc);

        // Initial state of fields and buttons
        // ALL editable fields (including CNPJ) are now disabled by default.
        // They will be enabled by handleNovoAction() or handleBuscarAction().
        setCamposEditavel(false); // Disables all fields in 'editableFields' list
        tfCnpj.setEnabled(true); // Re-enable CNPJ specifically, as it's the initial input point
        tfCnpj.setBackground(Color.WHITE); // Ensure CNPJ field is white initially

        btnIncluir.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnLimpar.setEnabled(false);
        btnCancelar.setEnabled(false);

        // Add Listeners
        btnNovo.addActionListener(e -> handleNovoAction());
        btnBuscar.addActionListener(e -> handleBuscarAction());
        btnIncluir.addActionListener(e -> incluirEmpresa());
        btnExcluir.addActionListener(e -> excluirEmpresa());
        btnLimpar.addActionListener(e -> limparCamposEResetarEstado());
        btnCancelar.addActionListener(e -> handleCancelarAction());
    }

    private void setCamposEditavel(boolean editable) {
        for (JComponent comp : editableFields) {
            // Do NOT disable tfCnpj here if it's the initial input field for search/new
            if (comp == tfCnpj && !editable) { // If we're setting to disabled AND it's the tfCnpj
                // We handle tfCnpj's disabled state separately within Novo/Buscar actions
                continue; // Skip disabling tfCnpj by this general method
            }

            comp.setEnabled(editable);
            if (comp instanceof JTextField) {
                ((JTextField) comp).setBackground(editable ? Color.WHITE : new Color(230, 230, 230));
            } else if (comp instanceof JCheckBox) {
                ((JCheckBox) comp).setBackground(editable ? new Color(250, 255, 250) : new Color(230, 230, 230));
            }
        }
    }

    private void limparCampos() {
        // Clear CNPJ field directly as it's not strictly part of 'editableFields' for initial state
        tfCnpj.setText("");
        tfNome.setText("");
        tfData.setValue(null); // Clears JFormattedTextField and its mask
        tfData.setText(""); // Ensures placeholder characters are visibly cleared
        tfFaturamento.setValue(BigDecimal.ZERO);
        tfBonus.setText("");
        cbLocadora.setSelected(false);
        tfPais.setText("");
        tfEstado.setText("");
        tfCidade.setText("");
        tfCep.setValue(null); // Clears JFormattedTextField and its mask
        tfCep.setText(""); // Ensures placeholder characters are visibly cleared
        tfLogradouro.setText("");
        tfNumero.setText("");
        tfComplemento.setText("");
    }

    private void limparCamposEResetarEstado() {
        limparCampos(); // Clears all fields

        // Reset CNPJ to enabled and white background
        tfCnpj.setEnabled(true);
        tfCnpj.setBackground(Color.WHITE);

        // Re-enable Novo and Buscar buttons
        btnNovo.setEnabled(true);
        btnBuscar.setEnabled(true);

        // Disable other editable fields
        // Iterate through editableFields and set them disabled, except tfCnpj
        for(JComponent comp : editableFields) {
            if (comp != tfCnpj) { // Do not disable tfCnpj here
                comp.setEnabled(false);
                if (comp instanceof JTextField) {
                    ((JTextField) comp).setBackground(new Color(230, 230, 230));
                } else if (comp instanceof JCheckBox) {
                    ((JCheckBox) comp).setBackground(new Color(230, 230, 230));
                }
            }
        }

        // Reset other buttons
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
            JOptionPane.showMessageDialog(this, validacaoCnpj, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            tfCnpj.requestFocusInWindow();
            return;
        }

        if (SeguradoEmpresaMediator.getInstancia().buscarSeguradoEmpresa(cnpj) != null) {
            JOptionPane.showMessageDialog(this, "CNPJ já cadastrado. Use 'Buscar' para editar ou excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            tfCnpj.requestFocusInWindow();
            return;
        }

        limparCamposExceptCnpj(); // Clear other fields but keep CNPJ
        tfCnpj.setEnabled(false); // Disable CNPJ as it's being registered
        tfCnpj.setBackground(new Color(230, 230, 230)); // Gray out CNPJ field
        setCamposEditavel(true); // Enable all other editable fields
        btnNovo.setEnabled(false);
        btnBuscar.setEnabled(false);
        btnIncluir.setText("Incluir");
        btnIncluir.setEnabled(true);
        btnExcluir.setEnabled(false); // No exclusion on new record
        btnLimpar.setEnabled(true);
        btnCancelar.setEnabled(true);
        tfNome.requestFocusInWindow();
    }

    private void handleBuscarAction() {
        String cnpj = tfCnpj.getText().trim();
        String validacaoCnpj = SeguradoEmpresaMediator.getInstancia().validarCnpj(cnpj);

        if (validacaoCnpj != null) {
            JOptionPane.showMessageDialog(this, validacaoCnpj, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            tfCnpj.requestFocusInWindow();
            return;
        }

        SeguradoEmpresa segurado = SeguradoEmpresaMediator.getInstancia().buscarSeguradoEmpresa(cnpj);

        if (segurado != null) {
            popularCampos(segurado);
            tfCnpj.setEnabled(false); // Disable CNPJ as it's now loaded
            tfCnpj.setBackground(new Color(230, 230, 230)); // Gray out CNPJ field
            setCamposEditavel(true); // Enable all other editable fields
            btnNovo.setEnabled(false);
            btnBuscar.setEnabled(false);
            btnIncluir.setText("Alterar"); // Change button text to Alterar
            btnIncluir.setEnabled(true);
            btnExcluir.setEnabled(true); // Allow exclusion of existing record
            btnLimpar.setEnabled(true);
            btnCancelar.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Empresa encontrada e dados preenchidos.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            tfNome.requestFocusInWindow();
        } else {
            limparCamposExceptCnpj(); // Clear other fields
            tfCnpj.setText(cnpj); // Keep CNPJ for user reference (already there)
            setCamposEditavel(false); // Disable all other editable fields
            tfCnpj.setEnabled(true); // Keep CNPJ enabled for next action
            tfCnpj.setBackground(Color.WHITE); // Ensure CNPJ is white

            btnIncluir.setEnabled(false);
            btnExcluir.setEnabled(false);
            btnLimpar.setEnabled(false);
            btnCancelar.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Nenhuma empresa encontrada com o CNPJ: " + cnpj + "\nUse 'Novo' para cadastrar.", "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
            tfCnpj.requestFocusInWindow();
        }
    }

    private void popularCampos(SeguradoEmpresa segurado) {
        tfNome.setText(segurado.getNome());
        Endereco endereco = segurado.getEndereco();
        if (endereco != null) {
            tfLogradouro.setText(endereco.getLogradouro());
            tfCep.setText(endereco.getCep() != null ? endereco.getCep().replaceAll("[^0-9]", "") : "");
            tfNumero.setText(endereco.getNumero());
            tfComplemento.setText(endereco.getComplemento());
            tfPais.setText(endereco.getPais());
            tfEstado.setText(endereco.getEstado());
            tfCidade.setText(endereco.getCidade());
        } else {
            tfLogradouro.setText("");
            tfCep.setValue(null); tfCep.setText(""); // Reset JFormattedTextField properly
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
        // Clear all fields except tfCnpj (which is handled separately)
        tfNome.setText("");
        tfData.setValue(null);
        tfData.setText("");
        tfFaturamento.setValue(BigDecimal.ZERO);
        tfBonus.setText("");

        cbLocadora.setSelected(false);
        tfPais.setText("");
        tfEstado.setText("");
        tfCidade.setText("");
        tfCep.setValue(null);
        tfCep.setText("");
        tfLogradouro.setText("");
        tfNumero.setText("");
        tfComplemento.setText("");
    }

    private void handleCancelarAction() {
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja descartar as alterações e retornar ao estado inicial?", "Cancelar Operação", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            limparCamposEResetarEstado();
        }
    }

    private void incluirEmpresa() {
        String cnpj = tfCnpj.getText().trim();
        String validacaoCnpj = SeguradoEmpresaMediator.getInstancia().validarCnpj(cnpj);
        if (validacaoCnpj != null) {
            JOptionPane.showMessageDialog(this, validacaoCnpj, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            tfCnpj.requestFocusInWindow();
            return;
        }

        try {
            String nome = tfNome.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Nome.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfNome.requestFocusInWindow();
                return;
            }

            String logradouro = tfLogradouro.getText().trim();
            String cep = tfCep.getText().replaceAll("[^0-9]", "").trim();
            String numero = tfNumero.getText().trim();
            String pais = tfPais.getText().trim();
            String estado = tfEstado.getText().trim();
            String cidade = tfCidade.getText().trim();

            if (logradouro.isEmpty() || cep.length() != 8 || numero.isEmpty() || pais.isEmpty() || estado.isEmpty() || cidade.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos de endereço obrigatórios (Logradouro, CEP, Número, País, Estado, Cidade). O CEP deve ter 8 dígitos numéricos.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                if (logradouro.isEmpty()) tfLogradouro.requestFocusInWindow();
                else if (cep.length() != 8) tfCep.requestFocusInWindow();
                else if (numero.isEmpty()) tfNumero.requestFocusInWindow();
                else if (pais.isEmpty()) tfPais.requestFocusInWindow();
                else if (estado.isEmpty()) tfEstado.requestFocusInWindow();
                else if (cidade.isEmpty()) tfCidade.requestFocusInWindow();
                return;
            }

            Endereco endereco = new Endereco(
                    logradouro,
                    cep,
                    numero,
                    tfComplemento.getText().trim(),
                    pais,
                    estado,
                    cidade
            );

            LocalDate data;
            try {
                tfData.commitEdit();
                String dateText = tfData.getText().trim().replace("_", ""); // Remove placeholder chars for validation
                if (dateText.length() != 10) {
                    JOptionPane.showMessageDialog(this, "Formato de Data de Abertura inválido. Use DD/MM/YYYY e preencha todos os dígitos.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                    tfData.requestFocusInWindow();
                    return;
                }
                data = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (ParseException | DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Formato de Data de Abertura inválido. Use DD/MM/YYYY.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfData.requestFocusInWindow();
                return;
            }

            BigDecimal bonus;
            try {
                bonus = new BigDecimal(tfBonus.getText().trim());
                if (bonus.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "Bônus não pode ser negativo.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                    tfBonus.requestFocusInWindow();
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Bônus deve ser um número válido.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfBonus.requestFocusInWindow();
                return;
            }

            BigDecimal faturamento;
            try {
                tfFaturamento.commitEdit();
                faturamento = (BigDecimal)tfFaturamento.getValue();
                if (faturamento == null || faturamento.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "Faturamento inválido. Deve ser um valor monetário positivo.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                    tfFaturamento.requestFocusInWindow();
                    return;
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Faturamento inválido. Por favor, insira um valor monetário válido (e.g., 12345,67).", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfFaturamento.requestFocusInWindow();
                return;
            }

            boolean ehLocadora = cbLocadora.isSelected();

            SeguradoEmpresa seg = new SeguradoEmpresa(nome, endereco, data, bonus, cnpj, faturamento.doubleValue(), ehLocadora);

            String erro;
            if (btnIncluir.getText().equals("Incluir")) {
                erro = SeguradoEmpresaMediator.getInstancia().incluirSeguradoEmpresa(seg);
            } else {
                erro = SeguradoEmpresaMediator.getInstancia().alterarSeguradoEmpresa(seg);
            }

            if (erro == null) {
                JOptionPane.showMessageDialog(this, "Operação realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCamposEResetarEstado();
            } else {
                JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro Geral", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void excluirEmpresa() {
        String cnpj = tfCnpj.getText().trim();
        String validacaoCnpj = SeguradoEmpresaMediator.getInstancia().validarCnpj(cnpj);

        if (validacaoCnpj != null) {
            JOptionPane.showMessageDialog(this, validacaoCnpj, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            tfCnpj.requestFocusInWindow();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir a empresa com CNPJ: " + cnpj + "?\nEsta ação é irreversível.", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            String erro = SeguradoEmpresaMediator.getInstancia().excluirSeguradoEmpresa(cnpj);
            if (erro == null) {
                JOptionPane.showMessageDialog(this, "Empresa excluída com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
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