package com.datadynecode.leilao.servico;

import com.datadynecode.leilao.builder.CriadorDeLeilao;
import com.datadynecode.leilao.dominio.Leilao;
import com.datadynecode.leilao.dominio.Pagamento;
import com.datadynecode.leilao.dominio.Usuario;
import com.datadynecode.leilao.infra.dao.LeilaoDao;
import com.datadynecode.leilao.infra.dao.PagamentosDao;
import com.datadynecode.leilao.infra.relogio.Relogio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GeradorDePagamentoTest {

    @Test
    public void deveGerarPagamentoParaUmLeilaoEncerrado() {
        LeilaoDao leiloes = mock(LeilaoDao.class);
        PagamentosDao pagamentos = mock(PagamentosDao.class);
        Avaliador avaliador = mock(Avaliador.class);

        Leilao leilao = new CriadorDeLeilao().para("Playstation")
                .lance(new Usuario("José da Silva"), 2000.0)
                .lance(new Usuario("Maria pereira"), 2500.0)
                .constroi();

        when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));
        when(avaliador.getMaiorLance()).thenReturn(2500.0);

        GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, avaliador);
        gerador.gera();

        ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentos).salva(argumento.capture());

        Pagamento pagamentoGerado = argumento.getValue();

        assertEquals(2500.0, pagamentoGerado.getValor(), 0.00001);
    }

    @Test
    public void deveEmpurrarParaOProximoDiaUtil() {
        LeilaoDao leiloes = mock(LeilaoDao.class);
        PagamentosDao pagamentos = mock(PagamentosDao.class);
        Relogio relogio = mock(Relogio.class);

        Leilao leilao = new CriadorDeLeilao().para("Playstation")
                .lance(new Usuario("José da Silva"), 2000)
                .lance(new Usuario("Maria Pereira"), 2500)
                .constroi();

        when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

        Calendar sabado = Calendar.getInstance();
        sabado.set(2012, Calendar.APRIL, 7);

        when(relogio.hoje()).thenReturn(sabado);

        GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, new Avaliador(), relogio);
        gerador.gera();

        ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentos).salva(argumento.capture());

        Pagamento pagamentoGerado = argumento.getValue();

        assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
        assertEquals(9, pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH));
    }

}
