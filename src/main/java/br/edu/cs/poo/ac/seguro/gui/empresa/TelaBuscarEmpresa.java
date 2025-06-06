package br.edu.cs.poo.ac.seguro.gui.empresa;

import br.edu.cs.poo.ac.seguro.daos.SeguradoEmpresaDAO;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;

public class TelaBuscarEmpresa extends JFrame {
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public TelaBuscarEmpresa() {
        setTitle("Buscar Segurado Empresa");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Cabeçalhos da tabela
        modeloTabela = new DefaultTableModel(new Object[]{"CNPJ", "Nome"}, 0);
        tabela = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabela);

        add(scrollPane, BorderLayout.CENTER);

        carregarEmpresas(); // Carrega dados reais da DAO
    }

    private void carregarEmpresas() {
        modeloTabela.setRowCount(0); // limpa antes de carregar

        SeguradoEmpresaDAO dao = new SeguradoEmpresaDAO();
        SeguradoEmpresa[] empresas = Arrays.stream(dao.buscarTodos())
                .map(e -> (SeguradoEmpresa) e)
                .toArray(SeguradoEmpresa[]::new);

        for (SeguradoEmpresa empresa : empresas) {
            modeloTabela.addRow(new Object[]{empresa.getCnpj(), empresa.getNome()});
        }
    }
}