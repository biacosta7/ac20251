package br.edu.cs.poo.ac.seguro.gui;

import br.edu.cs.poo.ac.seguro.entidades.TipoSinistro;
import br.edu.cs.poo.ac.seguro.excecoes.ExcecaoValidacaoDados;
import br.edu.cs.poo.ac.seguro.mediators.DadosSinistro;
import br.edu.cs.poo.ac.seguro.mediators.SinistroMediator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class TelaSinistro extends JFrame {

    private SinistroMediator sinistroMediator;
    private JTextField tfPlaca;
    private JFormattedTextField tfDataHoraSinistro;
    private JTextField tfUsuarioRegistro;
    private JFormattedTextField tfValorSinistro;
    private JComboBox<TipoSinistro> cbTipo;

    private JButton btnIncluir;
    private JButton btnLimpar;

    public TelaSinistro() {
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

        sinistroMediator = SinistroMediator.getInstancia();

        setTitle("Inclusão de Sinistro");
        setSize(500, 420); // Adjusted size for better aesthetics with padding
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // Keeps window fixed size

        // Main content panel with padding and background
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Generous padding
        mainPanel.setBackground(new Color(240, 248, 255)); // AliceBlue background
        add(mainPanel); // Add the main panel to the frame

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); // Padding for components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Components fill their display area horizontally
        gbc.anchor = GridBagConstraints.WEST; // Labels align to the left

        // --- Placa ---
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblPlaca = new JLabel("Placa:");
        lblPlaca.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(lblPlaca, gbc);
        tfPlaca = new JTextField(20); // Wider field
        tfPlaca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(tfPlaca, gbc);

        // --- Data/Hora do Sinistro ---
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel lblDataHora = new JLabel("Data/Hora (dd-MM-yyyy HH:mm):");
        lblDataHora.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(lblDataHora, gbc);
        tfDataHoraSinistro = criarCampoDataHora();
        tfDataHoraSinistro.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Apply font
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(tfDataHoraSinistro, gbc);

        // --- Registro do Usuário ---
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel lblUsuario = new JLabel("Registro do Usuário:");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(lblUsuario, gbc);
        tfUsuarioRegistro = new JTextField(20); // Wider field
        tfUsuarioRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Apply font
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(tfUsuarioRegistro, gbc);

        // --- Valor do Sinistro ---
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.0;
        JLabel lblValorSinistro = new JLabel("Valor do Sinistro:");
        lblValorSinistro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(lblValorSinistro, gbc);
        tfValorSinistro = criarCampoValor();
        tfValorSinistro.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Apply font
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(tfValorSinistro, gbc);

        // --- Tipo de Sinistro ---
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 0.0;
        JLabel lblTipo = new JLabel("Tipo de Sinistro:");
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(lblTipo, gbc);
        cbTipo = new JComboBox<>(Arrays.stream(TipoSinistro.values())
                .sorted(Comparator.comparing(Enum::name)) // Ordena por nome
                .toArray(TipoSinistro[]::new));
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Apply font
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(cbTipo, gbc);


        // --- Buttons Panel ---
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20)); // Increased spacing
        panelBotoes.setBackground(new Color(240, 248, 255)); // Match main panel background

        btnIncluir = new JButton("Registrar Sinistro"); // More descriptive button text
        btnIncluir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIncluir.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        btnIncluir.setForeground(Color.WHITE);
        btnIncluir.setFocusPainted(false);
        panelBotoes.add(btnIncluir);

        btnLimpar = new JButton("Limpar Campos"); // More descriptive button text
        btnLimpar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLimpar.setBackground(new Color(100, 149, 237)); // CornflowerBlue
        btnLimpar.setForeground(Color.WHITE);
        btnLimpar.setFocusPainted(false);
        panelBotoes.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2; // Span both columns
        gbc.fill = GridBagConstraints.NONE; // Buttons panel does not stretch horizontally
        gbc.anchor = GridBagConstraints.CENTER; // Center the button panel
        mainPanel.add(panelBotoes, gbc);

        addListeners();
    }

    private void addListeners() {
        btnIncluir.addActionListener(e -> incluirSinistro());
        btnLimpar.addActionListener(e -> limparCampos());
    }

    private void incluirSinistro() {
        try {
            String placa = tfPlaca.getText().trim();

            if (placa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Placa.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfPlaca.requestFocusInWindow();
                return;
            }
            // MaskFormatter fields can have placeholders if not fully filled, check for them
            if (tfDataHoraSinistro.getText().trim().replace("_", "").isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Data/Hora do Sinistro.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfDataHoraSinistro.requestFocusInWindow();
                return;
            }
            if (tfUsuarioRegistro.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Registro do Usuário.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfUsuarioRegistro.requestFocusInWindow();
                return;
            }
            // For JFormattedTextField using NumberFormatter, check its actual value after commitEdit
            if (tfValorSinistro.getText().trim().isEmpty()) { // Check text first for quick feedback
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Valor do Sinistro.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfValorSinistro.requestFocusInWindow();
                return;
            }

            // Commit edits for JFormattedTextFields before getting values
            try {
                tfDataHoraSinistro.commitEdit();
                tfValorSinistro.commitEdit();
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Formato inválido para Data/Hora ou Valor do Sinistro. Verifique se os valores estão completos e corretos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // For debugging
                return;
            }

            BigDecimal valorBigDecimal = (BigDecimal) tfValorSinistro.getValue();
            if (valorBigDecimal == null || valorBigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Valor do Sinistro inválido. Deve ser um valor monetário positivo.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfValorSinistro.requestFocusInWindow();
                return;
            }


            if (cbTipo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione o Tipo de Sinistro.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                cbTipo.requestFocusInWindow();
                return;
            }


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime dataHora = LocalDateTime.parse(tfDataHoraSinistro.getText(), formatter);
            String usuario = tfUsuarioRegistro.getText().trim();
            double valor = valorBigDecimal.doubleValue(); // Convert BigDecimal to double for DadosSinistro if necessary

            TipoSinistro tipo = (TipoSinistro) cbTipo.getSelectedItem();
            int codigoTipoSinistro = tipo.getCodigo();

            DadosSinistro dados = new DadosSinistro(placa, dataHora, usuario, valor, codigoTipoSinistro);

            // Call the mediator's inclusion method
            // Note: The second parameter 'LocalDateTime.now()' might be a placeholder or for audit purposes.
            // Ensure your mediator expects it, or remove/adjust if not needed.
            String numeroGerado = sinistroMediator.incluirSinistro(dados, LocalDateTime.now());

            JOptionPane.showMessageDialog(this,
                    "Sinistro incluído com sucesso!\nNúmero do sinistro: " + numeroGerado,
                    "Inclusão Bem Sucedida", JOptionPane.INFORMATION_MESSAGE);
            limparCampos(); // Clear fields after successful inclusion

        } catch (ExcecaoValidacaoDados evd) {
            StringBuilder mensagem = new StringBuilder("Não foi possível incluir o sinistro:\n");
            for (String erro : evd.getMensagens()) {
                mensagem.append("• ").append(erro).append("\n");
            }
            JOptionPane.showMessageDialog(this, mensagem.toString(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);

        } catch (java.time.format.DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(this,
                    "Formato de Data/Hora inválido. Utilize o formato dd-MM-yyyy HH:mm (e.g., 07-06-2025 10:30).",
                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            tfDataHoraSinistro.requestFocusInWindow();
            dtpe.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ocorreu um erro inesperado ao cadastrar o sinistro: " + ex.getMessage(),
                    "Erro Geral", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void limparCampos() {
        tfPlaca.setText("");
        tfDataHoraSinistro.setValue(null); // Clears masked field properly
        tfDataHoraSinistro.setText(""); // Ensure placeholder characters are reset
        tfUsuarioRegistro.setText("");
        tfValorSinistro.setValue(BigDecimal.ZERO); // Resets to 0.00
        if (cbTipo.getItemCount() > 0) {
            cbTipo.setSelectedIndex(0); // Selects the first item
        }
        tfPlaca.requestFocusInWindow(); // Set focus back to the first field
    }

    private JFormattedTextField criarCampoDataHora() {
        try {
            MaskFormatter formatter = new MaskFormatter("##-##-#### ##:##");
            formatter.setPlaceholderCharacter('_');
            formatter.setOverwriteMode(true); // Characters typed replace existing ones
            JFormattedTextField field = new JFormattedTextField(formatter);
            field.setColumns(14); // Appropriate width for the mask
            field.setValue(null); // Initial value to null
            field.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT); // Commit or revert on focus lost
            return field;
        } catch (ParseException e) {
            // Fallback if mask creation fails
            System.err.println("Error creating date/time mask: " + e.getMessage());
            return new JFormattedTextField();
        }
    }

    private JFormattedTextField criarCampoValor() {
        NumberFormat format = NumberFormat.getNumberInstance(new Locale("pt", "BR")); // Use pt-BR locale
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        format.setGroupingUsed(true); // For thousands separator

        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(BigDecimal.class); // Use BigDecimal for precision
        formatter.setAllowsInvalid(false); // Only allow valid numbers
        formatter.setMinimum(BigDecimal.ZERO); // Minimum value is 0
        formatter.setOverwriteMode(false); // Insert mode, not overwrite
        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setColumns(10); // Appropriate width
        field.setValue(BigDecimal.ZERO); // Initial value
        field.setFocusLostBehavior(JFormattedTextField.COMMIT); // Commit value when focus is lost
        return field;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                TelaSinistro window = new TelaSinistro();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}