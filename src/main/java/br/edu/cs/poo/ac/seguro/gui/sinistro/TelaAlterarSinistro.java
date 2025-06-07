package br.edu.cs.poo.ac.seguro.gui.sinistro;

import br.edu.cs.poo.ac.seguro.entidades.Sinistro;
import br.edu.cs.poo.ac.seguro.entidades.TipoSinistro;
import br.edu.cs.poo.ac.seguro.mediators.SinistroMediator;
import br.edu.cs.poo.ac.seguro.mediators.DadosSinistro;
import br.edu.cs.poo.ac.seguro.excecoes.ExcecaoValidacaoDados;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class TelaAlterarSinistro extends JFrame {

    private JTextField tfNumeroSinistro;
    private JTextField tfPlaca;
    private JFormattedTextField tfDataHoraSinistro;
    private JTextField tfUsuarioRegistro;
    private JFormattedTextField tfValorSinistro;
    private JComboBox<TipoSinistro> cbTipo;

    private JButton btnBuscar, btnSalvar;

    private SinistroMediator mediator = SinistroMediator.getInstancia();

    public TelaAlterarSinistro() {
        setTitle("Alterar Sinistro");
        setSize(420, 320);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lbNumero = new JLabel("Número do Sinistro:");
        tfNumeroSinistro = new JTextField();

        JLabel lbPlaca = new JLabel("Placa:");
        tfPlaca = new JTextField();

        JLabel lbDataHora = new JLabel("Data/Hora (dd-MM-yyyy HH:mm):");
        tfDataHoraSinistro = criarCampoDataHora();

        JLabel lbUsuario = new JLabel("Usuário Registro:");
        tfUsuarioRegistro = new JTextField();

        JLabel lbValor = new JLabel("Valor Sinistro:");
        tfValorSinistro = criarCampoValor();

        JLabel lbTipo = new JLabel("Tipo de Sinistro:");
        cbTipo = new JComboBox<>(Arrays.stream(TipoSinistro.values())
                .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
                .toArray(TipoSinistro[]::new));

        btnBuscar = new JButton("Buscar");
        btnSalvar = new JButton("Salvar");

        lbNumero.setBounds(20, 20, 140, 25);
        tfNumeroSinistro.setBounds(160, 20, 180, 25);
        btnBuscar.setBounds(350, 20, 80, 25);

        lbPlaca.setBounds(20, 55, 140, 25);
        tfPlaca.setBounds(160, 55, 180, 25);

        lbDataHora.setBounds(20, 90, 180, 25);
        tfDataHoraSinistro.setBounds(200, 90, 140, 25);

        lbUsuario.setBounds(20, 125, 140, 25);
        tfUsuarioRegistro.setBounds(160, 125, 180, 25);

        lbValor.setBounds(20, 160, 140, 25);
        tfValorSinistro.setBounds(160, 160, 180, 25);

        lbTipo.setBounds(20, 195, 140, 25);
        cbTipo.setBounds(160, 195, 180, 25);

        btnSalvar.setBounds(160, 230, 120, 30);

        add(lbNumero);
        add(tfNumeroSinistro);
        add(btnBuscar);
        add(lbPlaca);
        add(tfPlaca);
        add(lbDataHora);
        add(tfDataHoraSinistro);
        add(lbUsuario);
        add(tfUsuarioRegistro);
        add(lbValor);
        add(tfValorSinistro);
        add(lbTipo);
        add(cbTipo);
        add(btnSalvar);

        btnBuscar.addActionListener(e -> buscarSinistro());
        btnSalvar.addActionListener(e -> salvarAlteracoes());
    }

    private void buscarSinistro() {
        String numero = tfNumeroSinistro.getText().trim();
        if (numero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o número do sinistro para busca.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Sinistro sinistro = mediator.buscarSinistro(numero);
            tfPlaca.setText(sinistro.getVeiculo().getPlaca());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            tfDataHoraSinistro.setText(sinistro.getDataHoraSinistro().format(formatter));

            tfUsuarioRegistro.setText(sinistro.getUsuarioRegistro());
            tfValorSinistro.setText(sinistro.getValorSinistro().toString());
            cbTipo.setSelectedItem(sinistro.getTipo());

        } catch (ExcecaoValidacaoDados ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarAlteracoes() {
        try {
            String numero = tfNumeroSinistro.getText().trim();
            if (numero.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o número do sinistro.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String placa = tfPlaca.getText().trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime dataHora = LocalDateTime.parse(tfDataHoraSinistro.getText(), formatter);

            String usuario = tfUsuarioRegistro.getText().trim();

            BigDecimal valor = new BigDecimal(tfValorSinistro.getText().replace(",", "."));

            TipoSinistro tipo = (TipoSinistro) cbTipo.getSelectedItem();

            DadosSinistro dadosAtualizados = new DadosSinistro(placa,dataHora, usuario, valor.doubleValue(), tipo.getCodigo());

            mediator.alterarSinistro(numero, dadosAtualizados, LocalDateTime.now());

            JOptionPane.showMessageDialog(this, "Sinistro alterado com sucesso!");
            dispose();

        } catch (ExcecaoValidacaoDados ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
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
