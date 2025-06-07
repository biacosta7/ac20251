package br.edu.cs.poo.ac.seguro.gui.pessoa;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import br.edu.cs.poo.ac.seguro.entidades.Endereco;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa;
import br.edu.cs.poo.ac.seguro.mediators.SeguradoPessoaMediator;

public class TelaSeguradoPessoa extends JFrame {

    private JTextField tfCpf;
    private JTextField tfLogradouro, tfCep, tfNumero, tfComplemento, tfPais, tfEstado, tfCidade, tfNome, tfDataNascimento, tfBonus, tfRenda;

    private JButton btnNovo;
    private JButton btnBuscar;
    private JButton btnIncluir;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JButton btnCancelar;

    private JTextField[] dataFields;

    public TelaSeguradoPessoa() {
        setTitle("Cadastro de Segurado Pessoa");
        setSize(500, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblCpf = new JLabel("CPF:");
        lblCpf.setBounds(50, 20, 80, 25);
        add(lblCpf);

        tfCpf = new JTextField();
        tfCpf.setBounds(130, 20, 150, 25);
        add(tfCpf);

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
        tfDataNascimento = new JTextField();
        tfBonus = new JTextField();
        tfRenda = new JTextField();

        JLabel[] labels = {
                new JLabel("Nome:"),
                new JLabel("Logradouro:"), new JLabel("Número:"), new JLabel("Complemento:"),
                new JLabel("CEP:"), new JLabel("País:"), new JLabel("Estado:"), new JLabel("Cidade:"),
                new JLabel("Data Nascimento (YYYY-MM-DD):"),
                new JLabel("Bônus:"),
                new JLabel("Renda:")
        };

        dataFields = new JTextField[]{
                tfNome, tfLogradouro, tfNumero, tfComplemento, tfCep, tfPais, tfEstado, tfCidade,
                tfDataNascimento, tfBonus, tfRenda
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

        setFieldsEditable(false);
        btnIncluir.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnLimpar.setEnabled(false);
        btnCancelar.setEnabled(false);

        btnNovo.addActionListener(e -> handleNovoAction());
        btnBuscar.addActionListener(e -> handleBuscarAction());
        btnIncluir.addActionListener(e -> incluirPessoa());
        btnExcluir.addActionListener(e -> excluirPessoa());
        btnLimpar.addActionListener(e -> clearAllFieldsAndResetState());
        btnCancelar.addActionListener(e -> handleCancelarAction());
    }

    private void setFieldsEditable(boolean editable) {
        for (JTextField field : dataFields) {
            field.setEnabled(editable);
        }
    }

    private void clearAllFields() {
        tfCpf.setText("");
        for (JTextField field : dataFields) {
            field.setText("");
        }
    }

    private void clearAllFieldsAndResetState() {
        clearAllFields();
        tfCpf.setEnabled(true);
        tfCpf.setText("");
        btnNovo.setEnabled(true);
        btnBuscar.setEnabled(true);
        setFieldsEditable(false);
        btnIncluir.setText("Incluir");
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
            JOptionPane.showMessageDialog(this, validacaoCpf, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (SeguradoPessoaMediator.getInstancia().buscarSeguradoPessoa(cpf) != null) {
            JOptionPane.showMessageDialog(this, "CPF já cadastrado. Use 'Buscar' para editar ou excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        clearAllFieldsExceptCpf();
        tfCpf.setEnabled(false);
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
            JOptionPane.showMessageDialog(this, validacaoCpf, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SeguradoPessoa segurado = SeguradoPessoaMediator.getInstancia().buscarSeguradoPessoa(cpf);

        if (segurado != null) {
            populateFields(segurado);
            tfCpf.setEnabled(false);
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
            clearAllFieldsExceptCpf();
            tfCpf.setText(cpf);
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
            tfLogradouro.setText("");
            tfCep.setText("");
            tfNumero.setText("");
            tfComplemento.setText("");
            tfPais.setText("");
            tfEstado.setText("");
            tfCidade.setText("");
        }

        tfDataNascimento.setText(segurado.getDataNascimento() != null ? segurado.getDataNascimento().toString() : "");
        tfBonus.setText(segurado.getBonus() != null ? segurado.getBonus().toPlainString() : "");
        tfRenda.setText(String.valueOf(segurado.getRenda()));
    }

    private void clearAllFieldsExceptCpf() {
        for (JTextField field : dataFields) {
            field.setText("");
        }
    }

    private void handleCancelarAction() {
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja descartar as alterações e retornar ao estado inicial?", "Cancelar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            clearAllFieldsAndResetState();
        }
    }

    private void incluirPessoa() {
        String cpf = tfCpf.getText().trim();

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
            LocalDate dataNascimento = LocalDate.parse(tfDataNascimento.getText());
            BigDecimal bonus = new BigDecimal(tfBonus.getText());
            double renda = Double.parseDouble(tfRenda.getText());

            SeguradoPessoa seg = new SeguradoPessoa(nome, endereco, dataNascimento, bonus, cpf, renda);

            String erro;
            if (btnIncluir.getText().equals("Incluir")) {
                erro = SeguradoPessoaMediator.getInstancia().incluirSeguradoPessoa(seg);
            } else {
                erro = SeguradoPessoaMediator.getInstancia().alterarSeguradoPessoa(seg);
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
            JOptionPane.showMessageDialog(this, "Bônus ou Renda devem ser números válidos.", "Erro de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirPessoa() {
        String cpf = tfCpf.getText().trim();
        String validacaoCpf = SeguradoPessoaMediator.getInstancia().validarCpf(cpf);

        if (validacaoCpf != null) {
            JOptionPane.showMessageDialog(this, validacaoCpf, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir a pessoa com CPF: " + cpf + "?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String erro = SeguradoPessoaMediator.getInstancia().excluirSeguradoPessoa(cpf);
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