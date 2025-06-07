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
import java.util.ArrayList; // Import for ArrayList
import java.util.List;      // Import for List
import java.util.Locale;

import br.edu.cs.poo.ac.seguro.entidades.Endereco;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa;
import br.edu.cs.poo.ac.seguro.mediators.SeguradoPessoaMediator;

public class TelaSeguradoPessoa extends JFrame {

    private JTextField tfCpf;
    private JTextField tfNome, tfBonus;
    private JFormattedTextField tfDataNascimento, tfRenda;
    private JTextField tfLogradouro, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade; // Changed back to JTextField where MaskFormatter isn't strictly needed
    private JFormattedTextField tfCep; // Only CEP retains MaskFormatter

    private JButton btnNovo;
    private JButton btnBuscar;
    private JButton btnIncluir;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JButton btnCancelar;

    // Use a List<JComponent> for more flexible enabling/disabling
    private List<JComponent> editableFields;

    public TelaSeguradoPessoa() {
        // Apply Nimbus Look and Feel for a modern look
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fallback to system default if Nimbus isn't available
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        setTitle("Cadastro de Segurado Pessoa");
        setSize(700, 750); // Increased size for better spacing, similar to Empresa form
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main content panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Generous padding
        mainPanel.setBackground(new Color(240, 248, 255)); // AliceBlue background
        add(mainPanel); // Add the main panel to the frame

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); // Padding for components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Components fill their display area horizontally

        // Initialize editableFields list
        editableFields = new ArrayList<>();

        // --- CPF and Search/New Buttons ---
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblCpf = new JLabel("CPF:");
        lblCpf.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(lblCpf, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 0.5; // CPF field takes half available horizontal space
        tfCpf = new JTextField(18); // Wider CPF field
        tfCpf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(tfCpf, gbc);

        // Button Panel for Novo/Buscar
        JPanel cpfButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        cpfButtonsPanel.setBackground(new Color(240, 248, 255)); // Match main panel background

        btnNovo = new JButton("Novo");
        btnNovo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNovo.setBackground(new Color(95, 158, 160)); // CadetBlue
        btnNovo.setForeground(Color.WHITE);
        btnNovo.setFocusPainted(false);
        cpfButtonsPanel.add(btnNovo);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuscar.setBackground(new Color(70, 130, 180)); // SteelBlue
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        cpfButtonsPanel.add(btnBuscar);

        gbc.gridx = 2; gbc.gridy = 0;
        gbc.weightx = 0.5; // Buttons take remaining half
        gbc.gridwidth = 2; // Span across two logical columns for better alignment
        mainPanel.add(cpfButtonsPanel, gbc);

        // --- Personal Details Panel ---
        JPanel personalDetailsPanel = new JPanel(new GridBagLayout());
        personalDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Dados Pessoais", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(50, 50, 50))); // Title Border
        personalDetailsPanel.setBackground(new Color(250, 255, 250)); // Light green tint
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 4; // Span full width
        gbc.weightx = 1.0;
        mainPanel.add(personalDetailsPanel, gbc);

        GridBagConstraints pdpGbc = new GridBagConstraints();
        pdpGbc.insets = new Insets(5, 10, 5, 10);
        pdpGbc.fill = GridBagConstraints.HORIZONTAL;
        pdpGbc.anchor = GridBagConstraints.WEST;

        // Name
        pdpGbc.gridx = 0; pdpGbc.gridy = 0;
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 12));
        personalDetailsPanel.add(lblNome, pdpGbc);
        pdpGbc.gridx = 1; pdpGbc.weightx = 1.0;
        tfNome = new JTextField();
        tfNome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        personalDetailsPanel.add(tfNome, pdpGbc);
        editableFields.add(tfNome);

        // Data Nascimento
        pdpGbc.gridx = 0; pdpGbc.gridy = 1; pdpGbc.weightx = 0.0;
        JLabel lblDataNascimento = new JLabel("Data Nascimento (DD/MM/YYYY):");
        lblDataNascimento.setFont(new Font("Segoe UI", Font.BOLD, 12));
        personalDetailsPanel.add(lblDataNascimento, pdpGbc);
        pdpGbc.gridx = 1; pdpGbc.weightx = 1.0;
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            tfDataNascimento = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            tfDataNascimento = new JFormattedTextField();
            e.printStackTrace();
        }
        tfDataNascimento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        personalDetailsPanel.add(tfDataNascimento, pdpGbc);
        editableFields.add(tfDataNascimento);

        // Renda
        pdpGbc.gridx = 0; pdpGbc.gridy = 2; pdpGbc.weightx = 0.0;
        JLabel lblRenda = new JLabel("Renda:");
        lblRenda.setFont(new Font("Segoe UI", Font.BOLD, 12));
        personalDetailsPanel.add(lblRenda, pdpGbc);
        pdpGbc.gridx = 1; pdpGbc.weightx = 1.0;
        DecimalFormat rendaFormat = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        rendaFormat.setMinimumFractionDigits(2);
        rendaFormat.setMaximumFractionDigits(2);
        rendaFormat.applyPattern("#,##0.00"); // Ensure standard decimal pattern
        NumberFormatter rendaFormatter = new NumberFormatter(rendaFormat);
        rendaFormatter.setValueClass(BigDecimal.class); // Use BigDecimal for renda
        rendaFormatter.setAllowsInvalid(false);
        rendaFormatter.setOverwriteMode(false);
        tfRenda = new JFormattedTextField(rendaFormatter);
        tfRenda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tfRenda.setValue(BigDecimal.ZERO); // Initialize with BigDecimal.ZERO
        tfRenda.setFocusLostBehavior(JFormattedTextField.COMMIT); // Commit on focus lost
        personalDetailsPanel.add(tfRenda, pdpGbc);
        editableFields.add(tfRenda);

        // Bonus
        pdpGbc.gridx = 0; pdpGbc.gridy = 3; pdpGbc.weightx = 0.0;
        JLabel lblBonus = new JLabel("Bônus:");
        lblBonus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        personalDetailsPanel.add(lblBonus, pdpGbc);
        pdpGbc.gridx = 1; pdpGbc.weightx = 1.0;
        tfBonus = new JTextField();
        tfBonus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        personalDetailsPanel.add(tfBonus, pdpGbc);
        editableFields.add(tfBonus);

        // --- Address Details Panel ---
        JPanel addressDetailsPanel = new JPanel(new GridBagLayout());
        addressDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Endereço", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(50, 50, 50))); // Title Border
        addressDetailsPanel.setBackground(new Color(250, 250, 255)); // Light blue tint
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4; // Span full width
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
            tfCep = new JFormattedTextField(cepMask);
        } catch (ParseException e) {
            tfCep = new JFormattedTextField();
            e.printStackTrace();
        }
        tfCep.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15)); // Spacing between buttons
        actionButtonsPanel.setBackground(new Color(240, 248, 255)); // Match main panel background

        // Ordem dos campos
        JLabel[] labels = {
                new JLabel("Nome:"),
                new JLabel("Data Nascimento (DD/MM/YYYY):"),
                new JLabel("Renda:"),
                new JLabel("Bônus:")
        };

        JTextField[] fields = {
                tfNome, tfDataNascimento, tfRenda, tfBonus
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
                tfNome, tfDataNascimento, tfRenda, tfBonus,
                tfPais, tfEstado, tfCidade, tfCep, tfLogradouro, tfNumero, tfComplemento
        };

        btnIncluir = new JButton("Incluir");
        btnIncluir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnIncluir.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        btnIncluir.setForeground(Color.WHITE);
        btnIncluir.setFocusPainted(false);
        actionButtonsPanel.add(btnIncluir);

        btnExcluir = new JButton("Excluir");
        btnExcluir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExcluir.setBackground(new Color(220, 20, 60)); // Crimson
        btnExcluir.setForeground(Color.WHITE);
        btnExcluir.setFocusPainted(false);
        actionButtonsPanel.add(btnExcluir);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLimpar.setBackground(new Color(100, 149, 237)); // CornflowerBlue
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFocusPainted(false);
        actionButtonsPanel.add(btnLimpar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.setBackground(new Color(255, 165, 0)); // Orange
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        actionButtonsPanel.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4; // Span full width
        gbc.fill = GridBagConstraints.NONE; // Buttons do not stretch
        gbc.anchor = GridBagConstraints.CENTER; // Center the button panel
        mainPanel.add(actionButtonsPanel, gbc);

        // Initial state of fields and buttons
        setFieldsEditable(false);
        btnIncluir.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnLimpar.setEnabled(false);
        btnCancelar.setEnabled(false);

        // Add Listeners
        btnNovo.addActionListener(e -> handleNovoAction());
        btnBuscar.addActionListener(e -> handleBuscarAction());
        btnIncluir.addActionListener(e -> incluirPessoa());
        btnExcluir.addActionListener(e -> excluirPessoa());
        btnLimpar.addActionListener(e -> limparCamposEResetarEstado());
        btnCancelar.addActionListener(e -> handleCancelarAction());
    }

    // Changed parameter type to JComponent for flexibility
    private void setFieldsEditable(boolean editable) {
        for (JComponent comp : editableFields) {
            comp.setEnabled(editable);
            // Specifically handle JTextField/JFormattedTextField for background changes
            if (comp instanceof JTextField) {
                ((JTextField) comp).setBackground(editable ? Color.WHITE : new Color(230, 230, 230)); // Light gray when disabled
            }

        }
    }

    private void limparCampos() {
        tfCpf.setText("");

        tfNome.setText("");
        tfDataNascimento.setText("");
        tfRenda.setValue(BigDecimal.ZERO); // Reset to BigDecimal.ZERO
        tfBonus.setText("");
        tfPais.setText("");
        tfEstado.setText("");
        tfCidade.setText("");
        tfCep.setText("");
        tfLogradouro.setText("");
        tfNumero.setText("");
        tfComplemento.setText("");

    }

    private void limparCamposEResetarEstado() {
        limparCampos();
        tfCpf.setEnabled(true);
        tfCpf.setBackground(Color.WHITE); // Ensure CPF field is white
        btnNovo.setEnabled(true);
        btnBuscar.setEnabled(true);
        setFieldsEditable(false);
        btnIncluir.setText("Incluir"); // Reset button text
        btnIncluir.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnLimpar.setEnabled(false);
        btnCancelar.setEnabled(false);
        tfCpf.requestFocusInWindow();
    }

    private void handleNovoAction() {
        String cpf = tfCpf.getText().trim();
        String validacaoCpf = SeguradoPessoaMediator.getInstancia().validarCpf(cpf);

        if (validacaoCpf != null) {
            JOptionPane.showMessageDialog(this, validacaoCpf, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            tfCpf.requestFocusInWindow();
            return;
        }

        if (SeguradoPessoaMediator.getInstancia().buscarSeguradoPessoa(cpf) != null) {
            JOptionPane.showMessageDialog(this, "CPF já cadastrado. Use 'Buscar' para editar ou excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            tfCpf.requestFocusInWindow();
            return;
        }

        limparCamposExcetoCpf();
        tfCpf.setEnabled(false);
        tfCpf.setBackground(new Color(230, 230, 230)); // Gray out CPF field
        setFieldsEditable(true);

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
        String cpf = tfCpf.getText().trim();
        String validacaoCpf = SeguradoPessoaMediator.getInstancia().validarCpf(cpf);

        if (validacaoCpf != null) {
            JOptionPane.showMessageDialog(this, validacaoCpf, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            tfCpf.requestFocusInWindow();
            return;
        }

        SeguradoPessoa segurado = SeguradoPessoaMediator.getInstancia().buscarSeguradoPessoa(cpf);

        if (segurado != null) {
            popularCampos(segurado);
            tfCpf.setEnabled(false);
            tfCpf.setBackground(new Color(230, 230, 230)); // Gray out CPF field
            setFieldsEditable(true);

            btnNovo.setEnabled(false);
            btnBuscar.setEnabled(false);
            btnIncluir.setText("Alterar");
            btnIncluir.setEnabled(true);
            btnExcluir.setEnabled(true);
            btnLimpar.setEnabled(true);
            btnCancelar.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Pessoa encontrada e dados preenchidos.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            tfNome.requestFocusInWindow(); // Set focus to first editable field
        } else {

            clearAllFieldsExceptCpf();
            tfCpf.setText(cpf); 
            setFieldsEditable(false);

            btnIncluir.setEnabled(false);
            btnExcluir.setEnabled(false);
            btnLimpar.setEnabled(false);
            btnCancelar.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Nenhuma pessoa encontrada com o CPF: " + cpf + "\nUse 'Novo' para cadastrar.", "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
            tfCpf.requestFocusInWindow();
        }
    }

    private void popularCampos(SeguradoPessoa segurado) {
        tfNome.setText(segurado.getNome());
        Endereco endereco = segurado.getEndereco();
        if (endereco != null) {
            tfLogradouro.setText(endereco.getLogradouro());
            // Make sure to remove non-numeric chars from CEP when setting
            tfCep.setText(endereco.getCep() != null ? endereco.getCep().replaceAll("[^0-9]", "") : "");
            tfNumero.setText(endereco.getNumero());
            tfComplemento.setText(endereco.getComplemento());
            tfPais.setText(endereco.getPais());
            tfEstado.setText(endereco.getEstado());
            tfCidade.setText(endereco.getCidade());
        } else {
            // Clear address fields if no address
            tfLogradouro.setText("");
            tfCep.setText("");
            tfNumero.setText("");
            tfComplemento.setText("");
            tfPais.setText("");
            tfEstado.setText("");
            tfCidade.setText("");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        tfDataNascimento.setText(segurado.getDataNascimento() != null ? segurado.getDataNascimento().format(formatter) : "");
        tfBonus.setText(segurado.getBonus() != null ? segurado.getBonus().toPlainString() : "");
        tfRenda.setValue(segurado.getRenda()); // Set BigDecimal value for JFormattedTextField
    }

    private void clearAllFieldsExceptCpf() {
        tfNome.setText("");
        tfDataNascimento.setText("");
        tfRenda.setValue(BigDecimal.ZERO);
        tfBonus.setText("");
        tfPais.setText("");
        tfEstado.setText("");
        tfCidade.setText("");
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

    private void incluirPessoa() {
        String cpf = tfCpf.getText().trim();

        try {
            String nome = tfNome.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Nome.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfNome.requestFocusInWindow();
                return;
            }

            // Address fields validation (basic)
            String logradouro = tfLogradouro.getText().trim();
            String cep = tfCep.getText().replaceAll("[^0-9]", "").trim();
            String numero = tfNumero.getText().trim();
            String pais = tfPais.getText().trim();
            String estado = tfEstado.getText().trim();
            String cidade = tfCidade.getText().trim();

            if (logradouro.isEmpty() || cep.length() != 8 || numero.isEmpty() || pais.isEmpty() || estado.isEmpty() || cidade.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos de endereço obrigatórios (Logradouro, CEP, Número, País, Estado, Cidade). O CEP deve ter 8 dígitos.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                // Try to focus on the first empty address field
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
                    tfComplemento.getText().trim(), // Complemento can be empty
                    pais,
                    estado,
                    cidade
            );

            LocalDate dataNascimento;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dataNascimento = LocalDate.parse(tfDataNascimento.getText().trim(), formatter);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Formato de Data de Nascimento inválido. Use DD/MM/YYYY.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfDataNascimento.requestFocusInWindow();
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

            BigDecimal rendaBigDecimal;
            try {
                tfRenda.commitEdit(); // Ensure the formatter commits the value
                rendaBigDecimal = (BigDecimal)tfRenda.getValue(); // Get BigDecimal directly
                if (rendaBigDecimal == null || rendaBigDecimal.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "Renda inválida. Deve ser um valor monetário positivo.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                    tfRenda.requestFocusInWindow();
                    return;
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Renda inválida. Por favor, insira um valor monetário válido (e.g., 1234,56).", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfRenda.requestFocusInWindow();
                return;
            }


            SeguradoPessoa seg = new SeguradoPessoa(nome, endereco, dataNascimento, bonus, cpf, rendaBigDecimal.doubleValue()); // Convert BigDecimal to double for constructor

            String erro;
            if (btnIncluir.getText().equals("Incluir")) {
                erro = SeguradoPessoaMediator.getInstancia().incluirSeguradoPessoa(seg);
            } else { // It's "Alterar"
                erro = SeguradoPessoaMediator.getInstancia().alterarSeguradoPessoa(seg);
            }

            if (erro == null) {
                JOptionPane.showMessageDialog(this, "Operação realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                clearAllFieldsAndResetState();

            } else {
                JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro Geral", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void excluirPessoa() {
        String cpf = tfCpf.getText().trim();
        String validacaoCpf = SeguradoPessoaMediator.getInstancia().validarCpf(cpf);

        if (validacaoCpf != null) {
            JOptionPane.showMessageDialog(this, validacaoCpf, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            tfCpf.requestFocusInWindow();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir a pessoa com CPF: " + cpf + "?\nEsta ação é irreversível.", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            String erro = SeguradoPessoaMediator.getInstancia().excluirSeguradoPessoa(cpf);
            if (erro == null) {
                JOptionPane.showMessageDialog(this, "Pessoa excluída com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                clearAllFieldsAndResetState();
            } else {
                JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaSeguradoPessoa().setVisible(true));
    }
}