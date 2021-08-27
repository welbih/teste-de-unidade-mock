package com.datadynecode.leilao.servico;

import com.datadynecode.leilao.dominio.Leilao;
import com.datadynecode.leilao.dominio.Pagamento;
import com.datadynecode.leilao.infra.dao.LeilaoDao;
import com.datadynecode.leilao.infra.dao.PagamentosDao;
import com.datadynecode.leilao.infra.relogio.Relogio;
import com.datadynecode.leilao.infra.relogio.RelogioDoSistema;

import java.util.Calendar;
import java.util.List;

public class GeradorDePagamento {

    private final LeilaoDao leiloes;
    private final Avaliador avaliador;
    private final PagamentosDao pagamentos;
    private final Relogio relogio;

    public GeradorDePagamento(LeilaoDao leiloes, PagamentosDao pagamentos, Avaliador avaliador, Relogio relogio) {
        this.leiloes = leiloes;
        this.pagamentos = pagamentos;
        this.avaliador = avaliador;
        this.relogio = relogio;
    }

    public GeradorDePagamento(LeilaoDao leiloes, PagamentosDao pagamentos, Avaliador avaliador) {
        this(leiloes, pagamentos, avaliador, new RelogioDoSistema());
    }

    public void gera() {
        List<Leilao> leiloesEncerrados = this.leiloes.encerrados();

        for(Leilao leilao : leiloesEncerrados) {
            this.avaliador.avalia(leilao);

            Pagamento novoPagamento = new Pagamento(avaliador.getMaiorLance(), primeiroDiaUtil());
            this.pagamentos.salva(novoPagamento);
        }
    }

    private Calendar primeiroDiaUtil() {
        Calendar data = relogio.hoje();
        int diaDaSemana = data.get(Calendar.DAY_OF_WEEK);

        if(diaDaSemana == Calendar.SATURDAY) data.add(Calendar.DAY_OF_MONTH, 2);
        else if(diaDaSemana == Calendar.SUNDAY) data.add(Calendar.DAY_OF_MONTH, 1);

        return data;
    }
}
