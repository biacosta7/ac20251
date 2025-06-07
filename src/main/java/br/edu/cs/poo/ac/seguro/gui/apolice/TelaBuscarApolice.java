package br.edu.cs.poo.ac.seguro.gui.apolice; // Changed package to apolice

import br.edu.cs.poo.ac.seguro.entidades.Apolice; // Import Apolice
import br.edu.cs.poo.ac.seguro.entidades.Registro;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa;
import br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa;
import br.edu.cs.poo.ac.seguro.daos.ApoliceDAO; // Import ApoliceDAO
import br.edu.cs.poo.ac.seguro.mediators.ApoliceMediator; // Import ApoliceMediator

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TelaBuscarApolice extends JFrame {
    private JTable tabelaApolices; // Renamed for clarity
    private DefaultTableModel modeloTabela;

    private ApoliceDAO apoliceDAO; // Instance of ApoliceDAO
    private ApoliceMediator apoliceMediator; // Instance of ApoliceMediator (optional for listing, but good to have)


    public TelaBuscarApolice() {
        apoliceDAO = new ApoliceDAO(); // Initialize ApoliceDAO
        apoliceMediator = ApoliceMediator.getInstancia(); // Get Singleton instance

        setTitle("Buscar Apólices");
        setSize(900, 500); // Increased size to accommodate more columns
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Define table columns for Apolice
        modeloTabela = new DefaultTableModel(
                new Object[]{
                        "Número Apólice", "Placa Veículo", "CPF/CNPJ Segurado",
                        "Valor Franquia", "Valor Prêmio", "Valor Máximo Segurado",
                        "Data Início Vigência"
                }, 0 // 0 rows initially
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        tabelaApolices = new JTable(modeloTabela);
        tabelaApolices.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Auto-resize columns
        JScrollPane scrollPane = new JScrollPane(tabelaApolices);

        add(scrollPane, BorderLayout.CENTER);

        carregarApolices(); // Load data on initialization
    }

    private void carregarApolices() {
        modeloTabela.setRowCount(0); // Clear existing rows

        // Use ApoliceDAO to get all apolices
        Registro[] registros = apoliceDAO.buscarTodos();

        // Convert Registro[] to List<Apolice> for easier iteration and type safety
        List<Apolice> apolices = Arrays.stream(registros)
                .filter(r -> r instanceof Apolice)
                .map(r -> (Apolice) r)
                .collect(Collectors.toList());

        for (Apolice apolice : apolices) {
            String placaVeiculo = "N/A";
            String cpfOuCnpjSegurado = "N/A";

            if (apolice.getVeiculo() != null) {
                placaVeiculo = apolice.getVeiculo().getPlaca();
                // Safely get CPF/CNPJ from the vehicle's proprietor
                if (apolice.getVeiculo().getProprietario() != null) {
                    if (apolice.getVeiculo().getProprietario() instanceof SeguradoPessoa) {
                        cpfOuCnpjSegurado = ((SeguradoPessoa) apolice.getVeiculo().getProprietario()).getCpf();
                    } else if (apolice.getVeiculo().getProprietario() instanceof SeguradoEmpresa) {
                        cpfOuCnpjSegurado = ((SeguradoEmpresa) apolice.getVeiculo().getProprietario()).getCnpj();
                    }
                }
            }

            modeloTabela.addRow(new Object[]{
                    apolice.getNumero(),
                    placaVeiculo,
                    cpfOuCnpjSegurado,
                    apolice.getValorFranquia() != null ? apolice.getValorFranquia().toPlainString() : "N/A",
                    apolice.getValorPremio() != null ? apolice.getValorPremio().toPlainString() : "N/A",
                    apolice.getValorMaximoSegurado() != null ? apolice.getValorMaximoSegurado().toPlainString() : "N/A",
                    apolice.getDataInicioVigencia() != null ? apolice.getDataInicioVigencia().toString() : "N/A"
            });
        }
    }
}