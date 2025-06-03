package br.edu.cs.poo.ac.seguro.testes;

import br.edu.cs.poo.ac.seguro.entidades.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TesteSinistroDAO extends TesteDAO{
    private VeiculoDAO daoV = new VeiculoDAO();
    private SinistroDAO dao = new SinistroDAO();
    protected Class getClasse() {
        return Sinistro.class;
    }

    @Test
    public void teste01() {
        String placa = "00000000";
        String numero = "000000";
        cadastro.incluir(new Veiculo(placa, 2000, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);
        cadastro.incluir(new Sinistro(ve, LocalDateTime.now(), LocalDateTime.now(), null, null, TipoSinistro.COLISAO), numero);

        Sinistro sinistro = dao.buscar(numero);
        Assertions.assertNotNull(sinistro);
    }

    @Test
    public void teste02() {
        String placa = "10000000";
        String numero = "000001";
        cadastro.incluir(new Veiculo(placa, 2001, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);

        cadastro.incluir((Serializable)new Sinistro(ve, LocalDateTime.now(), LocalDateTime.now(), null, null, TipoSinistro.COLISAO), numero);
        Sinistro sinistro = dao.buscar("11000000");

        Assertions.assertNull(sinistro);
    }
    @Test
    public void teste03() {
        String placa = "20000000";
        String numero = "000002";
        cadastro.incluir(new Veiculo(placa, 2002, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);

        cadastro.incluir((Serializable)new Sinistro(ve, LocalDateTime.now(), LocalDateTime.now(), null, null, TipoSinistro.COLISAO), numero);
        boolean ret = dao.excluir(numero);
        Assertions.assertTrue(ret);
    }
    @Test
    public void teste04() {
        String placa = "30000000";
        String numero = "000003";
        cadastro.incluir(new Veiculo(placa, 2003, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);

        cadastro.incluir((Serializable)new Sinistro(ve, LocalDateTime.now(), LocalDateTime.now(), null, null, TipoSinistro.COLISAO), numero);
        boolean ret = dao.excluir("31000000");
        Assertions.assertFalse(ret);
    }
    @Test
    public void teste05() {
        String placa = "40000000";
        String numero = "000004";
        daoV.incluir(new Veiculo(placa, 2004, null, null, CategoriaVeiculo.BASICO));
        Veiculo ve = daoV.buscar(placa);
        Sinistro sinistro = new Sinistro(ve, LocalDateTime.now(), LocalDateTime.now(),null, null, TipoSinistro.COLISAO);
        sinistro.setNumero(numero);
        boolean ret = dao.incluir(sinistro);
        Assertions.assertTrue(ret);
        Sinistro sinistroBusca = dao.buscar(numero);
        Assertions.assertNotNull(sinistroBusca);
    }

    @Test
    public void teste06() {
        String placa = "50000000";
        String numero = "000005";
        cadastro.incluir(new Veiculo(placa, 2005, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);

        Sinistro sinistro = new Sinistro(ve, LocalDateTime.now(), LocalDateTime.now(), null, null, TipoSinistro.COLISAO);
        sinistro.setNumero(numero);

        cadastro.incluir((Serializable)sinistro, numero);

        boolean ret = dao.incluir(sinistro);
        Assertions.assertFalse(ret);
    }

    @Test
    public void teste07() {
        String placa = "60000000";
        String numero = "000006";
        cadastro.incluir(new Veiculo(placa, 2006, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);

        boolean ret = dao.alterar(new Sinistro(ve, LocalDateTime.now(), LocalDateTime.now(),null, null, TipoSinistro.COLISAO));
        Assertions.assertFalse(ret);
        Sinistro sinistro = dao.buscar(numero);
        Assertions.assertNull(sinistro);
    }

    @Test
    public void teste08() {
        String placa = "70000000";
        String numero = "000007";
        cadastro.incluir(new Veiculo(placa, 2007, null, null, CategoriaVeiculo.BASICO), placa);
        Veiculo ve = daoV.buscar(placa);

        Sinistro sinistro = new Sinistro(ve, LocalDateTime.now(), LocalDateTime.now(), null, null, TipoSinistro.COLISAO);
        cadastro.incluir((Serializable)sinistro, numero);

        sinistro = new Sinistro(ve, LocalDateTime.now(), LocalDateTime.now(), null, null, TipoSinistro.INCENDIO);
        sinistro.setNumero(numero);

        boolean ret = dao.alterar(sinistro);
        Assertions.assertTrue(ret);
    }
}
