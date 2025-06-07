package br.edu.cs.poo.ac.seguro.gui.sinistro;

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

public class TelaCadastrarSinistro extends JFrame {
    private JTextField tfPlaca;
    private JFormattedTextField tfDataHoraSinistro;
    private JTextField tfUsuarioRegistro;
    private JFormattedTextField tfValorSinistro;
    private JComboBox<TipoSinistro> cbTipo;
    private JButton btnIncluir, btnLimpar;
    private SinistroMediator mediator = SinistroMediator.getInstancia();

    public TelaCadastrarSinistro() {
        setTitle("Incluir Sinistro");
        setSize(400, 320);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);

        JLabel lbPlaca = new JLabel("Placa:");
        tfPlaca = new JTextField();

        JLabel lbDataHora = new JLabel("Data/Hora (dd-MM-yyyy HH:mm):");
        tfDataHoraSinistro = criarCampoDataHora();

        JLabel lbUsuario = new JLabel("Registro do Usuário:");
        tfUsuarioRegistro = new JTextField();

        JLabel lbValor = new JLabel("Valor do Sinistro:");
        tfValorSinistro = criarCampoValor();

        JLabel lbTipo = new JLabel("Tipo de Sinistro:");
        cbTipo = new JComboBox<>(Arrays.stream(TipoSinistro.values())
                .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
                .toArray(TipoSinistro[]::new));

        btnIncluir = new JButton("Incluir");
        btnLimpar = new JButton("Limpar");

        lbPlaca.setBounds(20, 20, 120, 20);
        tfPlaca.setBounds(160, 20, 200, 25);

        lbDataHora.setBounds(20, 55, 200, 20);
        tfDataHoraSinistro.setBounds(220, 55, 140, 25);

        lbUsuario.setBounds(20, 90, 120, 20);
        tfUsuarioRegistro.setBounds(160, 90, 200, 25);

        lbValor.setBounds(20, 125, 120, 20);
        tfValorSinistro.setBounds(160, 125, 200, 25);

        lbTipo.setBounds(20, 160, 120, 20);
        cbTipo.setBounds(160, 160, 200, 25);

        btnIncluir.setBounds(70, 210, 100, 30);
        btnLimpar.setBounds(200, 210, 100, 30);

        add(lbPlaca); add(tfPlaca);
        add(lbDataHora); add(tfDataHoraSinistro);
        add(lbUsuario); add(tfUsuarioRegistro);
        add(lbValor); add(tfValorSinistro);
        add(lbTipo); add(cbTipo);
        add(btnIncluir); add(btnLimpar);

        btnIncluir.addActionListener(e -> incluirSinistro());
        btnLimpar.addActionListener(e -> limparCampos());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void incluirSinistro() {
        try {
            String placa = tfPlaca.getText().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime dataHora = LocalDateTime.parse(tfDataHoraSinistro.getText(), formatter);
            String usuario = tfUsuarioRegistro.getText().trim();
            double valor = Double.parseDouble(tfValorSinistro.getText().replace(",", "."));
            TipoSinistro tipo = (TipoSinistro) cbTipo.getSelectedItem();

            DadosSinistro dados = new DadosSinistro(placa, dataHora, usuario, valor, tipo.ordinal());

            String numeroGerado = SinistroMediator.getInstancia().incluirSinistro(dados, LocalDateTime.now());

            JOptionPane.showMessageDialog(this,
                    "Sinistro incluído com sucesso!\nNúmero do sinistro: " + numeroGerado);
            dispose();

        } catch (ExcecaoValidacaoDados evd) {
            StringBuilder mensagem = new StringBuilder("Não foi possível incluir o sinistro:\n");
            for (String erro : evd.getMensagens()) {
                mensagem.append("• ").append(erro).append("\n");
            }
            JOptionPane.showMessageDialog(this, mensagem.toString(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro inesperado:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        tfPlaca.setText("");
        tfDataHoraSinistro.setValue(null);
        tfUsuarioRegistro.setText("");
        tfValorSinistro.setValue(null);
        cbTipo.setSelectedIndex(0);
    }

    private JFormattedTextField criarCampoDataHora() {
        try {
            MaskFormatter formatter = new MaskFormatter("##-##-#### ##:##");
            formatter.setPlaceholderCharacter('_');
            return new JFormattedTextField(formatter);
        } catch (ParseException e) {
            return new JFormattedTextField();
        }
    }

    private JFormattedTextField criarCampoValor() {
        NumberFormat format = NumberFormat.getNumberInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0.0);
        return new JFormattedTextField(formatter);
    }

}
