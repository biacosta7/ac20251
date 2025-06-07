package br.edu.cs.poo.ac.seguro.gui.sinistro;

import br.edu.cs.poo.ac.seguro.entidades.Sinistro;
import br.edu.cs.poo.ac.seguro.mediators.SinistroMediator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaBuscarSinistro extends JFrame {
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public TelaBuscarSinistro() {
        setTitle("Buscar Sinistros");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        modeloTabela = new DefaultTableModel(
                new Object[]{"Número", "Placa", "Data/Hora", "Valor", "Tipo", "Usuário Registro"}, 0
        );
        tabela = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabela);

        add(scrollPane, BorderLayout.CENTER);

        carregarSinistros();
    }

    private void carregarSinistros() {
        modeloTabela.setRowCount(0);

        List<Sinistro> sinistros = SinistroMediator.getInstancia().listarTodosSinistros();
        for (Sinistro s : sinistros) {
            modeloTabela.addRow(new Object[]{
                    s.getNumero(),
                    s.getVeiculo() != null ? s.getVeiculo().getPlaca() : "N/A",
                    s.getDataHoraSinistro(),
                    s.getValorSinistro(),
                    s.getTipo() != null ? s.getTipo().getNome() : "N/A",
                    s.getUsuarioRegistro()
            });
        }
    }
}
