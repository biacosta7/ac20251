package br.edu.cs.poo.ac.seguro.testes;
import br.edu.cs.poo.ac.seguro.entidades.Veiculo;
import org.junit.jupiter.api.Test;
import br.edu.cs.poo.ac.seguro.entidades.CategoriaVeiculo;
import org.junit.jupiter.api.Assertions;
import br.edu.cs.poo.ac.seguro.entidades.Apolice;
import java.io.Serializable;
import java.math.BigDecimal;

public class TesteApoliceDAO extends TesteDAO{
    private ApoliceDAO dao = new ApoliceDAO();
    private VeiculoDAO daoV = new VeiculoDAO();

    protected Class getClasse() {
        return br.edu.cs.poo.ac.seguro.entidades.Apolice.class;
    }
    @Test
    public void teste01() {
        String placa = "00000000";
        String numero = "000000";
        cadastro.incluir(new Veiculo(placa, 2000, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);
        cadastro.incluir(new Apolice(ve, new BigDecimal("19.99"), null, null, null), numero);
        Apolice apolice = dao.buscar(numero);
        Assertions.assertNotNull(apolice);
    }
    @Test
    public void teste02() {
        String placa = "10000000";
        cadastro.incluir(new Veiculo(placa, 2001, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);
        String numero = "000001";
        cadastro.incluir((Serializable)new Apolice(ve, new BigDecimal("19.99"), null, null, null), numero);
        Apolice apolice = dao.buscar("11000000");
        Assertions.assertNull(apolice);
    }
    @Test
    public void teste03() {
        String placa = "20000000";
        cadastro.incluir(new Veiculo(placa, 2001, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);
        String numero = "000002";
        cadastro.incluir((Serializable)new Apolice(ve, new BigDecimal("19.99"), null, null, null), numero);
        boolean ret = dao.excluir(numero);
        Assertions.assertTrue(ret);
    }
    @Test
    public void teste04() {
        String placa = "30000000";
        cadastro.incluir(new Veiculo(placa, 2003, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);
        String numero = "000003";
        cadastro.incluir((Serializable)new Apolice(ve, new BigDecimal("19.99"), null, null, null), numero);
        boolean ret = dao.excluir("31000000");
        Assertions.assertFalse(ret);
    }
    @Test
    public void teste05() {
        String placa = "40000000";
        cadastro.incluir(new Veiculo(placa, 2003, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);
        String numero = "000004";
        Apolice novaApolice = new Apolice(ve, new BigDecimal("19.99"), null, null, null);
        novaApolice.setNumero(numero);
        boolean ret = dao.incluir(novaApolice);
        Assertions.assertTrue(ret);
        Apolice apolice = dao.buscar(numero);
        Assertions.assertNotNull(apolice);
    }

    @Test
    public void teste06() {
        String placa = "50000000";
        cadastro.incluir(new Veiculo(placa, 2003, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);
        String numero = "000005";
        Apolice apolice = new Apolice(ve, new BigDecimal("19.99"), null, null, null);
        apolice.setNumero(numero);
        cadastro.incluir((Serializable)apolice, numero);
        boolean ret = dao.incluir(apolice);
        Assertions.assertFalse(ret);
    }

    @Test
    public void teste07() {
        String placa = "60000000";
        cadastro.incluir(new Veiculo(placa, 2003, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);
        String numero = "0000006";
        boolean ret = dao.alterar(new Apolice(ve, new BigDecimal("19.99"), null, null, null));
        Assertions.assertFalse(ret);
        Apolice apolice = dao.buscar(numero);
        Assertions.assertNull(apolice);
    }

    @Test
    public void teste08() {
        String placa = "70000000";
        cadastro.incluir(new Veiculo(placa, 2003, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);
        String numero = "0000007";
        Apolice apolice = new Apolice(ve, new BigDecimal("19.99"), null, null, null);
        cadastro.incluir((Serializable)apolice, numero);
        apolice = new Apolice(ve, new BigDecimal("19.90"), null, null, null);
        apolice.setNumero(numero);
        boolean ret = dao.alterar(apolice);
        Assertions.assertTrue(ret);
    }
}
