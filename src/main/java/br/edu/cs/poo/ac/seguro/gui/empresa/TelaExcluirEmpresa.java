package br.edu.cs.poo.ac.seguro.gui.empresa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import br.edu.cs.poo.ac.seguro.daos.SeguradoEmpresaDAO;
import br.edu.cs.poo.ac.seguro.entidades.Registro;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;

public class TelaExcluirEmpresa extends JFrame {

    private JTable tabelaEmpresas;
    private DefaultTableModel modeloTabela;
    private JButton btnExcluir;

    SeguradoEmpresaDAO dao = new SeguradoEmpresaDAO();
    Registro[] registros = dao.buscarTodos();

    public TelaExcluirEmpresa() {
        setTitle("Excluir Empresa");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        modeloTabela = new DefaultTableModel(new Object[]{"CNPJ", "Nome"}, 0);
        tabelaEmpresas = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaEmpresas);

        btnExcluir = new JButton("Excluir Empresa Selecionada");

        add(scrollPane, BorderLayout.CENTER);
        add(btnExcluir, BorderLayout.SOUTH);

        carregarEmpresas();

        btnExcluir.addActionListener(e -> excluirEmpresaSelecionada());
    }

    private void carregarEmpresas() {
        modeloTabela.setRowCount(0); // limpa
        for (Registro registro : registros) {
            SeguradoEmpresa empresa = (SeguradoEmpresa) registro;
            modeloTabela.addRow(new Object[]{empresa.getCnpj(), empresa.getNome()});
        }
    }

    private void excluirEmpresaSelecionada() {
        int linhaSelecionada = tabelaEmpresas.getSelectedRow();
        if (linhaSelecionada != -1) {
            String cnpj = (String) modeloTabela.getValueAt(linhaSelecionada, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir a empresa com CNPJ " + cnpj + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.excluir(cnpj); // ajuste esse método
                carregarEmpresas();
                JOptionPane.showMessageDialog(this, "Empresa excluída com sucesso.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma empresa para excluir.");
        }
    }
}
