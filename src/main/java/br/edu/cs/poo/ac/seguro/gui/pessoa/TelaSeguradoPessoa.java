package br.edu.cs.poo.ac.seguro.gui.pessoa;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import br.edu.cs.poo.ac.seguro.entidades.Endereco;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa;
import br.edu.cs.poo.ac.seguro.mediators.SeguradoPessoaMediator;

public class TelaSeguradoPessoa extends JFrame {

    private JTextField tfCpf; // Changed from tfCnpj to tfCpf
    private JTextField tfLogradouro, tfCep, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade, tfNome, tfDataNascimento, tfBonus, tfRenda; // Renamed tfData to tfDataNascimento, added tfRenda

    private JButton btnNovo;
    private JButton btnBuscar;
    private JButton btnIncluir; // Will change to "Alterar"
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JButton btnCancelar;

    private JTextField[] dataFields; // Array of all data entry fields (excluding CPF)

    public TelaSeguradoPessoa() {
        setTitle("Cadastro de Segurado Pessoa"); // Updated title
        setSize(500, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // --- CPF and Top Buttons ---
        JLabel lblCpf = new JLabel("CPF:"); // Changed label to CPF
        lblCpf.setBounds(50, 20, 80, 25);
        add(lblCpf);

        tfCpf = new JTextField(); // Changed from tfCnpj to tfCpf
        tfCpf.setBounds(130, 20, 150, 25);
        add(tfCpf);

        btnNovo = new JButton("Novo");
        btnNovo.setBounds(300, 20, 80, 25);
        add(btnNovo);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(390, 20, 80, 25);
        add(btnBuscar);

        // --- Fields for SeguradoPessoa details ---
        tfNome = new JTextField();
        tfLogradouro = new JTextField();
        tfNumero = new JTextField();
        tfComplemento = new JTextField();
        tfCep = new JTextField();
        tfPais = new JTextField();
        tfEstado = new JTextField();
        tfCidade = new JTextField();
        tfDataNascimento = new JTextField(); // Renamed
        tfBonus = new JTextField();
        tfRenda = new JTextField(); // Added tfRenda

        JLabel[] labels = {
                new JLabel("Nome:"),
                new JLabel("Logradouro:"), new JLabel("Número:"), new JLabel("Complemento:"),
                new JLabel("CEP:"), new JLabel("País:"), new JLabel("Estado:"), new JLabel("Cidade:"),
                new JLabel("Data Nascimento (YYYY-MM-DD):"), // Changed label
                new JLabel("Bônus:"),
                new JLabel("Renda:") // Added label
        };

        dataFields = new JTextField[]{ // Array of fields to enable/disable
                tfNome, tfLogradouro, tfNumero, tfComplemento, tfCep, tfPais, tfEstado, tfCidade,
                tfDataNascimento, tfBonus, tfRenda // Updated fields
        };

        // Layout for data fields
        int y = 60;
        int labelX = 50;
        int fieldX = 200;
        int labelWidth = 140;
        int fieldWidth = 250;
        int rowHeight = 30;
        int spacing = 5;

        for (int i = 0; i < labels.length; i++) {
            labels[i].setBounds(labelX, y, labelWidth, 25);
            dataFields[i].setBounds(fieldX, y, fieldWidth, 25);
            add(labels[i]);
            add(dataFields[i]);
            y += rowHeight + spacing;
        }

        // SeguradoPessoa does not have 'ehLocadora'
        // So, remove the cbLocadora and its bounds.
        // If there was a specific checkbox for Pessoa, it would go here.
        // For now, y can just continue from the last text field.
        // cbLocadora.setBounds(fieldX, y, fieldWidth, 25);
        // add(cbLocadora);
        // y += rowHeight + spacing;

        // --- Bottom Action Buttons ---
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

        // --- Initial State Setup ---
        setFieldsEditable(false); // All data fields start as disabled
        // No cbLocadora for Pessoa, so remove its enable/disable line here
        // cbLocadora.setEnabled(false);
        btnIncluir.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnLimpar.setEnabled(false);
        btnCancelar.setEnabled(false);

        // --- Action Listeners ---
        btnNovo.addActionListener(e -> handleNovoAction());
        btnBuscar.addActionListener(e -> handleBuscarAction());
        btnIncluir.addActionListener(e -> incluirPessoa()); // Changed to incluirPessoa
        btnExcluir.addActionListener(e -> excluirPessoa()); // Changed to excluirPessoa
        btnLimpar.addActionListener(e -> clearAllFieldsAndResetState());
        btnCancelar.addActionListener(e -> handleCancelarAction());
    }

    private void setFieldsEditable(boolean editable) {
        for (JTextField field : dataFields) {
            field.setEnabled(editable);
        }
        // No cbLocadora for Pessoa
        // cbLocadora.setEnabled(editable);
    }

    private void clearAllFields() {
        tfCpf.setText(""); // Changed from tfCnpj
        for (JTextField field : dataFields) {
            field.setText("");
        }
        // No cbLocadora for Pessoa
        // cbLocadora.setSelected(false);
    }

    private void clearAllFieldsAndResetState() {
        clearAllFields();
        tfCpf.setEnabled(true); // Changed from tfCnpj
        tfCpf.setText(""); // Ensure CPF is also cleared
        btnNovo.setEnabled(true);
        btnBuscar.setEnabled(true);
        setFieldsEditable(false);
        btnIncluir.setText("Incluir"); // Reset button text
        btnIncluir.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnLimpar.setEnabled(false);
        btnCancelar.setEnabled(false);
        tfCpf.requestFocusInWindow(); // Focus on CPF for new operation
    }

    private void handleNovoAction() {
        String cpf = tfCpf.getText().trim(); // Changed from cnpj to cpf
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, digite o CPF para um novo cadastro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Basic CPF validation (assuming 11 digits)
        if (!cpf.matches("\\d{11}")) { // Changed from 14 to 11 for CPF
            JOptionPane.showMessageDialog(this, "CPF inválido. Digite 11 dígitos.", "Erro de CPF", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if CPF already exists (using SeguradoPessoaMediator)
        if (SeguradoPessoaMediator.getInstancia().buscarSeguradoPessoa(cpf) != null) { // Changed mediator call
            JOptionPane.showMessageDialog(this, "CPF já cadastrado. Use 'Buscar' para editar ou excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        clearAllFieldsExceptCpf(); // Changed from Cnpj to Cpf
        tfCpf.setEnabled(false); // Lock CPF for new entry
        setFieldsEditable(true); // Enable other fields for input
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
        String cpf = tfCpf.getText().trim(); // Changed from cnpj to cpf
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, digite o CPF para buscar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Basic CPF validation
        if (!cpf.matches("\\d{11}")) { // Changed from 14 to 11 for CPF
            JOptionPane.showMessageDialog(this, "CPF inválido. Digite 11 dígitos.", "Erro de CPF", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SeguradoPessoa segurado = SeguradoPessoaMediator.getInstancia().buscarSeguradoPessoa(cpf); // Changed mediator call

        if (segurado != null) {
            populateFields(segurado);
            tfCpf.setEnabled(false); // Lock CPF
            setFieldsEditable(true);
            btnNovo.setEnabled(false);
            btnBuscar.setEnabled(false);
            btnIncluir.setText("Alterar");
            btnIncluir.setEnabled(true);
            btnExcluir.setEnabled(true);
            btnLimpar.setEnabled(true);
            btnCancelar.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Pessoa encontrada e dados preenchidos.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            clearAllFieldsExceptCpf(); // Changed from Cnpj to Cpf
            tfCpf.setText(cpf); // Keep the entered CPF
            setFieldsEditable(false);
            btnIncluir.setEnabled(false);
            btnExcluir.setEnabled(false);
            btnLimpar.setEnabled(false);
            btnCancelar.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Nenhuma pessoa encontrada com o CPF: " + cpf + "\nUse 'Novo' para cadastrar.", "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void populateFields(SeguradoPessoa segurado) {
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
            // Clear address fields if no address is associated
            tfLogradouro.setText("");
            tfCep.setText("");
            tfNumero.setText("");
            tfComplemento.setText("");
            tfPais.setText("");
            tfEstado.setText("");
            tfCidade.setText("");
        }

        tfDataNascimento.setText(segurado.getDataNascimento() != null ? segurado.getDataNascimento().toString() : ""); // Changed to DataNascimento
        tfBonus.setText(segurado.getBonus() != null ? segurado.getBonus().toPlainString() : "");
        tfRenda.setText(String.valueOf(segurado.getRenda())); // Set Renda field
        // No cbLocadora for Pessoa
        // cbLocadora.setSelected(false);
    }

    private void clearAllFieldsExceptCpf() { // Changed from Cnpj to Cpf
        for (JTextField field : dataFields) {
            field.setText("");
        }
        // No cbLocadora for Pessoa
        // cbLocadora.setSelected(false);
    }

    private void handleCancelarAction() {
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja descartar as alterações e retornar ao estado inicial?", "Cancelar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            clearAllFieldsAndResetState();
        }
    }

    private void incluirPessoa() { // Changed method name
        String cpf = tfCpf.getText().trim(); // Changed from cnpj to cpf
        if (cpf.isEmpty() || !cpf.matches("\\d{11}")) { // Changed validation
            JOptionPane.showMessageDialog(this, "CPF inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String nome = tfNome.getText();
            Endereco endereco = new Endereco(
                    tfLogradouro.getText(),
                    tfCep.getText(),
                    tfNumero.getText(),
                    tfComplemento.getText(),
                    tfPais.getText(),
                    tfEstado.getText(),
                    tfCidade.getText()
            );
            LocalDate dataNascimento = LocalDate.parse(tfDataNascimento.getText()); // Changed to DataNascimento
            BigDecimal bonus = new BigDecimal(tfBonus.getText());
            double renda = Double.parseDouble(tfRenda.getText()); // Get Renda

            SeguradoPessoa seg = new SeguradoPessoa(nome, endereco, dataNascimento, bonus, cpf, renda); // Changed constructor parameters

            String erro;
            if (btnIncluir.getText().equals("Incluir")) {
                erro = SeguradoPessoaMediator.getInstancia().incluirSeguradoPessoa(seg); // Changed mediator call
            } else { // "Alterar"
                erro = SeguradoPessoaMediator.getInstancia().alterarSeguradoPessoa(seg); // Changed mediator call
            }

            if (erro == null) {
                JOptionPane.showMessageDialog(this, "Operação realizada com sucesso!");
                clearAllFieldsAndResetState();
            } else {
                JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use YYYY-MM-DD.", "Erro de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bônus ou Renda devem ser números válidos.", "Erro de Dados", JOptionPane.ERROR_MESSAGE); // Changed message
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirPessoa() { // Changed method name
        String cpf = tfCpf.getText().trim(); // Changed from cnpj to cpf
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "CPF não pode estar vazio para exclusão.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir a pessoa com CPF: " + cpf + "?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String erro = SeguradoPessoaMediator.getInstancia().excluirSeguradoPessoa(cpf); // Changed mediator call
            if (erro == null) {
                JOptionPane.showMessageDialog(this, "Pessoa excluída com sucesso.");
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