package br.edu.cs.poo.ac.seguro.gui.pessoa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import br.edu.cs.poo.ac.seguro.daos.SeguradoPessoaDAO;
import br.edu.cs.poo.ac.seguro.entidades.Registro;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa;

public class TelaExcluirPessoa extends JFrame {

    private JTable tabelaPessoas;
    private DefaultTableModel modeloTabela;
    private JButton btnExcluir;

    SeguradoPessoaDAO dao = new SeguradoPessoaDAO();
    Registro[] registros = dao.buscarTodos();

    public TelaExcluirPessoa() {
        setTitle("Excluir Pessoa");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        modeloTabela = new DefaultTableModel(new Object[]{"CPF", "Nome"}, 0);
        tabelaPessoas = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaPessoas);

        btnExcluir = new JButton("Excluir Pessoa Selecionada");

        add(scrollPane, BorderLayout.CENTER);
        add(btnExcluir, BorderLayout.SOUTH);

        carregarPessoas();

        btnExcluir.addActionListener(e -> excluirPessoaSelecionada());
    }

    private void carregarPessoas() {
        modeloTabela.setRowCount(0); // limpa
        for (Registro registro : registros) {
            SeguradoPessoa pessoa = (SeguradoPessoa) registro;
            modeloTabela.addRow(new Object[]{pessoa.getCpf(), pessoa.getNome()});
        }
    }

    private void excluirPessoaSelecionada() {
        int linhaSelecionada = tabelaPessoas.getSelectedRow();
        if (linhaSelecionada != -1) {
            String cpf = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir a pessoa com CPF " + cpf + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.excluir(cpf); // ajuste esse método
                carregarPessoas();
                JOptionPane.showMessageDialog(this, "Pessoa excluída com sucesso.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa para excluir.");
        }
    }
}
