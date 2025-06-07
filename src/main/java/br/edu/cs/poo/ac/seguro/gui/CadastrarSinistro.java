package br.edu.cs.poo.ac.seguro.gui;

import br.edu.cs.poo.ac.seguro.entidades.TipoSinistro;
import br.edu.cs.poo.ac.seguro.excecoes.ExcecaoValidacaoDados;
import br.edu.cs.poo.ac.seguro.mediators.DadosSinistro;
import br.edu.cs.poo.ac.seguro.mediators.SinistroMediator;

import javax.swing.*;
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

public class CadastrarSinistro extends JFrame {

    private SinistroMediator sinistroMediator;
    private JTextField tfPlaca;
    private JFormattedTextField tfDataHoraSinistro;
    private JTextField tfUsuarioRegistro;
    private JFormattedTextField tfValorSinistro;
    private JComboBox<TipoSinistro> cbTipo; // JComboBox de TipoSinistro

    private JButton btnIncluir;
    private JButton btnLimpar;

    public CadastrarSinistro() {
        sinistroMediator = SinistroMediator.getInstancia();

        setTitle("Inclusão de Sinistro");
        setSize(450, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Placa ---
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Placa:"), gbc);
        tfPlaca = new JTextField(15);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(tfPlaca, gbc);

        // --- Data/Hora do Sinistro ---
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        add(new JLabel("Data/Hora (dd-MM-yyyy HH:mm):"), gbc);
        tfDataHoraSinistro = criarCampoDataHora(); // Reusa o método da TelaCadastrarSinistro
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(tfDataHoraSinistro, gbc);

        // --- Registro do Usuário ---
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        add(new JLabel("Registro do Usuário:"), gbc);
        tfUsuarioRegistro = new JTextField(15);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(tfUsuarioRegistro, gbc);

        // --- Valor do Sinistro ---
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.0;
        add(new JLabel("Valor do Sinistro:"), gbc);
        tfValorSinistro = criarCampoValor(); // Reusa o método da TelaCadastrarSinistro
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(tfValorSinistro, gbc);

        // --- Tipo de Sinistro ---
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 0.0;
        add(new JLabel("Tipo de Sinistro:"), gbc);
        cbTipo = new JComboBox<>(Arrays.stream(TipoSinistro.values())
                .sorted(Comparator.comparing(Enum::name)) // Ordena por nome
                .toArray(TipoSinistro[]::new));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(cbTipo, gbc);


        // --- Buttons Panel ---
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnIncluir = new JButton("Incluir");
        panelBotoes.add(btnIncluir);

        btnLimpar = new JButton("Limpar");
        panelBotoes.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(panelBotoes, gbc);

        addListeners();
    }

    private void addListeners() {
        btnIncluir.addActionListener(e -> incluirSinistro());
        btnLimpar.addActionListener(e -> limparCampos());
    }

    private void incluirSinistro() {
        try {
            String placa = tfPlaca.getText().trim();

            // Validação de campos vazios/inválidos antes de tentar parsear
            if (placa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Placa.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfPlaca.requestFocusInWindow();
                return;
            }
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
            if (tfValorSinistro.getText().trim().isEmpty() || tfValorSinistro.getValue() == null) { // Adicionado check para null
                JOptionPane.showMessageDialog(this, "Por favor, preencha o campo Valor do Sinistro.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                tfValorSinistro.requestFocusInWindow();
                return;
            }
            if (cbTipo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione o Tipo de Sinistro.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                cbTipo.requestFocusInWindow();
                return;
            }


            // Commit edits para JFormattedTextFields
            try {
                tfDataHoraSinistro.commitEdit();
                tfValorSinistro.commitEdit();
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Formato inválido para Data/Hora ou Valor do Sinistro.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                return;
            }


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime dataHora = LocalDateTime.parse(tfDataHoraSinistro.getText(), formatter);
            String usuario = tfUsuarioRegistro.getText().trim();
            BigDecimal valorBigDecimal = (BigDecimal) tfValorSinistro.getValue(); // Valor já como BigDecimal
            double valor = valorBigDecimal.doubleValue(); // Converte para double conforme DadosSinistro

            TipoSinistro tipo = (TipoSinistro) cbTipo.getSelectedItem();
            int tipoOrdinal = tipo.ordinal(); // O ordinal do enum

            DadosSinistro dados = new DadosSinistro(placa, dataHora, usuario, valor, tipoOrdinal);

            String numeroGerado = SinistroMediator.getInstancia().incluirSinistro(dados, LocalDateTime.now()); // Passa LocalDateTime.now() para o registro no mediador

            JOptionPane.showMessageDialog(this,
                    "Sinistro incluído com sucesso!\nNúmero do sinistro: " + numeroGerado,
                    "Inclusão Bem Sucedida", JOptionPane.INFORMATION_MESSAGE);
            limparCampos(); // Limpa os campos após sucesso

        } catch (ExcecaoValidacaoDados evd) {
            StringBuilder mensagem = new StringBuilder("Não foi possível incluir o sinistro:\n");
            for (String erro : evd.getMensagens()) {
                mensagem.append("• ").append(erro).append("\n");
            }
            JOptionPane.showMessageDialog(this, mensagem.toString(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ocorreu um erro inesperado ao cadastrar o sinistro: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Imprime o stack trace para depuração
        }
    }

    private void limparCampos() {
        tfPlaca.setText("");
        tfDataHoraSinistro.setValue(null);
        tfUsuarioRegistro.setText("");
        tfValorSinistro.setValue(BigDecimal.ZERO);
        if (cbTipo.getItemCount() > 0) {
            cbTipo.setSelectedIndex(0);
        }
        tfPlaca.requestFocusInWindow();
    }

    private JFormattedTextField criarCampoDataHora() {
        try {
            MaskFormatter formatter = new MaskFormatter("##-##-#### ##:##");
            formatter.setPlaceholderCharacter('_');
            formatter.setOverwriteMode(true);
            JFormattedTextField field = new JFormattedTextField(formatter);
            field.setColumns(14);
            field.setValue(null);
            return field;
        } catch (ParseException e) {
            return new JFormattedTextField();
        }
    }

    private JFormattedTextField criarCampoValor() {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        format.setGroupingUsed(true);

        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(BigDecimal.class);
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(BigDecimal.ZERO);
        formatter.setOverwriteMode(false);
        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setColumns(10);
        field.setValue(BigDecimal.ZERO);
        return field;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                CadastrarSinistro window = new CadastrarSinistro();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}