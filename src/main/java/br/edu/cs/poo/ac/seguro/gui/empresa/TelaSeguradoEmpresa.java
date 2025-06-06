package br.edu.cs.poo.ac.seguro.gui.empresa;

import javax.swing.*;

public class TelaSeguradoEmpresa extends JFrame {
    public TelaSeguradoEmpresa() {
        setTitle("CRUD - Segurado Empresa");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnAlterar = new JButton("Alterar");
        JButton btnExcluir = new JButton("Excluir");

        btnCadastrar.setBounds(120, 30, 150, 30);
        btnBuscar.setBounds(120, 70, 150, 30);
        btnAlterar.setBounds(120, 110, 150, 30);
        btnExcluir.setBounds(120, 150, 150, 30);

        add(btnCadastrar);
        add(btnBuscar);
        add(btnAlterar);
        add(btnExcluir);

        btnCadastrar.addActionListener(e -> new TelaCadastrarEmpresa().setVisible(true));
        btnBuscar.addActionListener(e -> new TelaBuscarEmpresa().setVisible(true));
        btnAlterar.addActionListener(e -> new TelaAlterarEmpresa().setVisible(true));
        btnExcluir.addActionListener(e -> new TelaExcluirEmpresa().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaSeguradoEmpresa().setVisible(true));
    }
}

