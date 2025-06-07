package br.edu.cs.poo.ac.seguro.gui.sinistro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import br.edu.cs.poo.ac.seguro.entidades.Registro;
import br.edu.cs.poo.ac.seguro.entidades.Sinistro;
import br.edu.cs.poo.ac.seguro.daos.SinistroDAO;
import br.edu.cs.poo.ac.seguro.excecoes.ExcecaoValidacaoDados;
import br.edu.cs.poo.ac.seguro.mediators.SinistroMediator;


public class TelaExcluirSinistro extends JFrame {

    private JTable tabelaSinistros;
    private DefaultTableModel modeloTabela;
    private JButton btnExcluir;

    private SinistroDAO dao = new SinistroDAO();
    private Registro[] registros = dao.buscarTodos();

    public TelaExcluirSinistro() {
        setTitle("Excluir Sinistro");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        modeloTabela = new DefaultTableModel(new Object[]{"Placa", "Data/Hora", "Usuário"}, 0);
        tabelaSinistros = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaSinistros);

        btnExcluir = new JButton("Excluir Sinistro Selecionada");

        add(scrollPane, BorderLayout.CENTER);
        add(btnExcluir, BorderLayout.SOUTH);

        carregarSinistros();

        btnExcluir.addActionListener(e -> excluirSinistroSelecionada());
    }

    private void carregarSinistros() {
        modeloTabela.setRowCount(0); // limpa
        for (Registro registro : registros) {
            Sinistro sinistro = (Sinistro) registro;
            modeloTabela.addRow(new Object[]{
                    sinistro.getVeiculo().getPlaca(),
                    sinistro.getDataHoraSinistro().toString(),
                    sinistro.getUsuarioRegistro()
            });
        }
    }

    private void excluirSinistroSelecionada() {
        int linhaSelecionada = tabelaSinistros.getSelectedRow();
        if (linhaSelecionada != -1) {
            String numeroSinistro = (String) modeloTabela.getValueAt(linhaSelecionada, 0); // supondo que a primeira coluna seja número do sinistro
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja excluir o sinistro número " + numeroSinistro + "?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    SinistroMediator.getInstancia().excluirSinistro(numeroSinistro);
                    registros = dao.buscarTodos();
                    carregarSinistros();
                    JOptionPane.showMessageDialog(this, "Sinistro excluído com sucesso.");
                } catch (ExcecaoValidacaoDados e) {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir sinistro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um sinistro para excluir.");
        }
    }

}
