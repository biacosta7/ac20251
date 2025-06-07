package br.edu.cs.poo.ac.seguro.gui.empresa;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import br.edu.cs.poo.ac.seguro.entidades.Endereco;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;
import br.edu.cs.poo.ac.seguro.mediators.SeguradoEmpresaMediator;

public class TelaSeguradoEmpresa extends JFrame {

    private JTextField tfCnpj;
    private JTextField tfLogradouro, tfCep, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade, tfNome, tfData, tfBonus, tfFaturamento;
    private JCheckBox cbLocadora;

    private JButton btnNovo;
    private JButton btnBuscar;
    private JButton btnIncluir;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JButton btnCancelar;

    private JTextField[] dataFields;

    public TelaSeguradoEmpresa() {
        setTitle("Cadastro de Segurado Empresa");
        setSize(500, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // --- CNPJ e Top Buttons ---
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
        tfLogradouro = new JTextField();
        tfNumero = new JTextField();
        tfComplemento = new JTextField();
        tfCep = new JTextField();
        tfPais = new JTextField();
        tfEstado = new JTextField();
        tfCidade = new JTextField();
        tfData = new JTextField();
        tfBonus = new JTextField();
        tfFaturamento = new JTextField();
        cbLocadora = new JCheckBox("É locadora");

        JLabel[] labels = {
                new JLabel("Nome:"),
                new JLabel("Logradouro:"), new JLabel("Número:"), new JLabel("Complemento:"),
                new JLabel("CEP:"), new JLabel("País:"), new JLabel("Estado:"), new JLabel("Cidade:"),
                new JLabel("Data Abertura (YYYY-MM-DD):"), new JLabel("Bônus:"), new JLabel("Faturamento:")
        };

        dataFields = new JTextField[]{ 
                tfNome, tfLogradouro, tfNumero, tfComplemento, tfCep, tfPais, tfEstado, tfCidade,
                tfData, tfBonus, tfFaturamento
        };

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

        cbLocadora.setBounds(fieldX, y, fieldWidth, 25);
        add(cbLocadora);
        y += rowHeight + spacing;

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
        setFieldsEditable(false);
        cbLocadora.setEnabled(false);
        btnIncluir.setEnabled(false);
        btnExcluir.setEnabled(false); // Excluir is disabled inicialmente
        btnLimpar.setEnabled(false);
        btnCancelar.setEnabled(false);

        // --- Action Listeners ---
        btnNovo.addActionListener(e -> handleNovoAction());
        btnBuscar.addActionListener(e -> handleBuscarAction());
        btnIncluir.addActionListener(e -> incluirEmpresa());
        btnExcluir.addActionListener(e -> excluirEmpresa());
        btnLimpar.addActionListener(e -> clearAllFieldsAndResetState());
        btnCancelar.addActionListener(e -> handleCancelarAction());
    }

    private void setFieldsEditable(boolean editable) {
        for (JTextField field : dataFields) {
            field.setEnabled(editable);
        }
        cbLocadora.setEnabled(editable);
    }

    private void clearAllFields() {
        tfCnpj.setText("");
        for (JTextField field : dataFields) {
            field.setText("");
        }
        cbLocadora.setSelected(false);
    }

    private void clearAllFieldsAndResetState() {
        clearAllFields();
        tfCnpj.setEnabled(true);
        tfCnpj.setText(""); // Ensure CNPJ is also cleared if user clicks "Limpar" from any state
        btnNovo.setEnabled(true);
        btnBuscar.setEnabled(true);
        setFieldsEditable(false);
        btnIncluir.setText("Incluir"); // Reset button text
        btnIncluir.setEnabled(false);
        btnExcluir.setEnabled(false); // Disable excluir
        btnLimpar.setEnabled(false);
        btnCancelar.setEnabled(false);
        tfCnpj.requestFocusInWindow(); // Focus on CNPJ for new operation
    }

    private void handleNovoAction() {
        String cnpj = tfCnpj.getText().trim();
        if (cnpj.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, digite o CNPJ para um novo cadastro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!cnpj.matches("\\d{14}")) {
            JOptionPane.showMessageDialog(this, "CNPJ inválido. Digite 14 dígitos.", "Erro de CNPJ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (SeguradoEmpresaMediator.getInstancia().buscarSeguradoEmpresa(cnpj) != null) {
            JOptionPane.showMessageDialog(this, "CNPJ já cadastrado. Use 'Buscar' para editar ou excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        clearAllFieldsExceptCnpj();
        tfCnpj.setEnabled(false);
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
        String cnpj = tfCnpj.getText().trim();
        if (cnpj.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, digite o CNPJ para buscar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!cnpj.matches("\\d{14}")) {
            JOptionPane.showMessageDialog(this, "CNPJ inválido. Digite 14 dígitos.", "Erro de CNPJ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SeguradoEmpresa segurado = SeguradoEmpresaMediator.getInstancia().buscarSeguradoEmpresa(cnpj);

        if (segurado != null) {
            populateFields(segurado);
            tfCnpj.setEnabled(false);
            setFieldsEditable(true);
            btnNovo.setEnabled(false);
            btnBuscar.setEnabled(false);
            btnIncluir.setText("Alterar");
            btnIncluir.setEnabled(true);
            btnExcluir.setEnabled(true);
            btnLimpar.setEnabled(true);
            btnCancelar.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Empresa encontrada e dados preenchidos.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            clearAllFieldsExceptCnpj();
            tfCnpj.setText(cnpj);
            setFieldsEditable(false);
            btnIncluir.setEnabled(false);
            btnExcluir.setEnabled(false);
            btnLimpar.setEnabled(false);
            btnCancelar.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Nenhuma empresa encontrada com o CNPJ: " + cnpj + "\nUse 'Novo' para cadastrar.", "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void populateFields(SeguradoEmpresa segurado) {
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

        tfData.setText(segurado.getDataAbertura() != null ? segurado.getDataAbertura().toString() : "");
        tfBonus.setText(segurado.getBonus() != null ? segurado.getBonus().toPlainString() : "");
        tfFaturamento.setText(String.valueOf(segurado.getFaturamento()));
        cbLocadora.setSelected(segurado.isEhLocadoraDeVeiculos());
    }

    private void clearAllFieldsExceptCnpj() {
        for (JTextField field : dataFields) {
            field.setText("");
        }
        cbLocadora.setSelected(false);
    }

    private void handleCancelarAction() {
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja descartar as alterações e retornar ao estado inicial?", "Cancelar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            clearAllFieldsAndResetState();
        }
    }

    private void incluirEmpresa() {
        String cnpj = tfCnpj.getText().trim();
        if (cnpj.isEmpty() || !cnpj.matches("\\d{14}")) {
            JOptionPane.showMessageDialog(this, "CNPJ inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
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
            LocalDate data = LocalDate.parse(tfData.getText());
            BigDecimal bonus = new BigDecimal(tfBonus.getText());
            double faturamento = Double.parseDouble(tfFaturamento.getText());
            boolean ehLocadora = cbLocadora.isSelected();

            SeguradoEmpresa seg = new SeguradoEmpresa(nome, endereco, data, bonus, cnpj, faturamento, ehLocadora);

            String erro;
            if (btnIncluir.getText().equals("Incluir")) { // Check button text to determine action
                erro = SeguradoEmpresaMediator.getInstancia().incluirSeguradoEmpresa(seg);
            } else { // "Alterar"
                erro = SeguradoEmpresaMediator.getInstancia().alterarSeguradoEmpresa(seg);
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
            JOptionPane.showMessageDialog(this, "Bônus ou Faturamento devem ser números válidos.", "Erro de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirEmpresa() {
        String cnpj = tfCnpj.getText().trim();
        if (cnpj.isEmpty()) {
            JOptionPane.showMessageDialog(this, "CNPJ não pode estar vazio para exclusão.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir a empresa com CNPJ: " + cnpj + "?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String erro = SeguradoEmpresaMediator.getInstancia().excluirSeguradoEmpresa(cnpj);
            if (erro == null) {
                JOptionPane.showMessageDialog(this, "Empresa excluída com sucesso.");
                clearAllFieldsAndResetState();
            } else {
                JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaSeguradoEmpresa().setVisible(true));
    }
}