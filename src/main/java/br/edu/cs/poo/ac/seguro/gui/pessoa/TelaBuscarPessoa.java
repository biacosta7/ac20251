package br.edu.cs.poo.ac.seguro.gui.pessoa;

import br.edu.cs.poo.ac.seguro.daos.SeguradoPessoaDAO;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;

public class TelaBuscarPessoa extends JFrame {
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public TelaBuscarPessoa() {
        setTitle("Buscar Segurado Pessoa");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Cabeçalhos da tabela
        modeloTabela = new DefaultTableModel(new Object[]{"CPF", "Nome"}, 0);
        tabela = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabela);

        add(scrollPane, BorderLayout.CENTER);

        carregarPessoas(); // Carrega dados reais da DAO
    }

    private void carregarPessoas() {
        modeloTabela.setRowCount(0); // limpa antes de carregar

        SeguradoPessoaDAO dao = new SeguradoPessoaDAO();
        SeguradoPessoa[] pessoas = Arrays.stream(dao.buscarTodos())
                .map(e -> (SeguradoPessoa) e)
                .toArray(SeguradoPessoa[]::new);

        for (SeguradoPessoa pessoa : pessoas) {
            modeloTabela.addRow(new Object[]{pessoa.getCpf(), pessoa.getNome()});
        }
    }
}