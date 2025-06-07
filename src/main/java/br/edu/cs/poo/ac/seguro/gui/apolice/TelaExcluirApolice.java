package br.edu.cs.poo.ac.seguro.gui.apolice; // Changed package to apolice

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays; // Import Arrays
import java.util.List;
import java.util.stream.Collectors; // Import Collectors

import br.edu.cs.poo.ac.seguro.entidades.Registro;
import br.edu.cs.poo.ac.seguro.entidades.Apolice; // Import Apolice
import br.edu.cs.poo.ac.seguro.daos.ApoliceDAO; // Import ApoliceDAO
// No need for ExcecaoValidacaoDados if mediator returns String for errors
import br.edu.cs.poo.ac.seguro.mediators.ApoliceMediator; // Import ApoliceMediator

public class TelaExcluirApolice extends JFrame {

    private JTable tabelaApolices;
    private DefaultTableModel modeloTabela;
    private JButton btnExcluir;

    // Use the ApoliceDAO and ApoliceMediator
    private ApoliceDAO apoliceDAO;
    private ApoliceMediator apoliceMediator;

    public TelaExcluirApolice() {
        // Initialize DAOs and Mediators
        apoliceDAO = new ApoliceDAO();
        apoliceMediator = ApoliceMediator.getInstancia(); // Use the Singleton instance

        setTitle("Excluir Apólice");
        setSize(700, 500); // Increased size for more columns
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Define table columns for Apolice
        modeloTabela = new DefaultTableModel(new Object[]{
                "Número da Apólice", "Placa Veículo", "CPF/CNPJ Segurado", "Valor Prêmio", "Data Início Vigência"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        tabelaApolices = new JTable(modeloTabela);
        // Optional: Make table columns automatically resize
        tabelaApolices.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(tabelaApolices);

        btnExcluir = new JButton("Excluir Apólice Selecionada");

        add(scrollPane, BorderLayout.CENTER);
        add(btnExcluir, BorderLayout.SOUTH);

        carregarApolices(); // Load data when frame is created

        btnExcluir.addActionListener(e -> excluirApoliceSelecionada());
    }

    private void carregarApolices() {
        modeloTabela.setRowCount(0); // Clear existing rows

        // Retrieve all Apolice records using the DAO
        Registro[] registros = apoliceDAO.buscarTodos();

        // Convert Registro[] to List<Apolice> for easier iteration and type safety
        List<Apolice> apolices = Arrays.stream(registros)
                .filter(r -> r instanceof Apolice)
                .map(r -> (Apolice) r)
                .collect(Collectors.toList());

        for (Apolice apolice : apolices) {
            String cpfOuCnpjSegurado = "N/A"; // Default if segurado is not found or type is unknown
            // Assuming Veiculo has a getProprietario() method which returns a Segurado
            if (apolice.getVeiculo() != null && apolice.getVeiculo().getProprietario() != null) {
                if (apolice.getVeiculo().getProprietario() instanceof br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa) {
                    cpfOuCnpjSegurado = ((br.edu.cs.poo.ac.seguro.entidades.SeguradoPessoa) apolice.getVeiculo().getProprietario()).getCpf();
                } else if (apolice.getVeiculo().getProprietario() instanceof br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa) {
                    cpfOuCnpjSegurado = ((br.edu.cs.poo.ac.seguro.entidades.SeguradoEmpresa) apolice.getVeiculo().getProprietario()).getCnpj();
                }
            }

            modeloTabela.addRow(new Object[]{
                    apolice.getNumero(),
                    apolice.getVeiculo() != null ? apolice.getVeiculo().getPlaca() : "N/A",
                    cpfOuCnpjSegurado,
                    apolice.getValorPremio() != null ? apolice.getValorPremio().toPlainString() : "N/A", // Convert BigDecimal to String
                    apolice.getDataInicioVigencia() != null ? apolice.getDataInicioVigencia().toString() : "N/A"
            });
        }
    }

    private void excluirApoliceSelecionada() {
        int linhaSelecionada = tabelaApolices.getSelectedRow();
        if (linhaSelecionada != -1) {
            // Get the policy number from the first column of the selected row
            String numeroApolice = (String) modeloTabela.getValueAt(linhaSelecionada, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja excluir a apólice número " + numeroApolice + "?",
                    "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Call the mediator's exclude method
                String resultadoExclusao = apoliceMediator.excluirApolice(numeroApolice);

                if (resultadoExclusao == null) { // Mediator returns null on success
                    JOptionPane.showMessageDialog(this, "Apólice excluída com sucesso!");
                    carregarApolices(); // Refresh the table
                } else { // Mediator returns an error message on failure
                    JOptionPane.showMessageDialog(this, "Erro ao excluir apólice: " + resultadoExclusao, "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma apólice para excluir.", "Nenhuma Apólice Selecionada", JOptionPane.WARNING_MESSAGE);
        }
    }
}