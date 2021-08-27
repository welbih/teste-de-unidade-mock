package com.datadynecode.leilao.servico;

import java.util.Calendar;
import java.util.List;

import com.datadynecode.leilao.dominio.Leilao;
import com.datadynecode.leilao.infra.dao.LeilaoDao;
import com.datadynecode.leilao.infra.email.Carteiro;

public class EncerradorDeLeilao {

	private final Carteiro carteiro;
	private int total = 0;
	private final LeilaoDao dao;

	public EncerradorDeLeilao(LeilaoDao dao, Carteiro carteiro) {
		this.dao = dao;
		this.carteiro = carteiro;
	}
	public void encerra() {
		List<Leilao> todosLeiloesCorrentes = dao.correntes();

		for (Leilao leilao : todosLeiloesCorrentes) {
			try {
				if (comecouSemanaPassada(leilao)) {
					leilao.encerra();
					total++;
					dao.atualiza(leilao);
					carteiro.envia(leilao);
				}
			} catch(Exception e) {
				// loga excecao e continua...
			}
		}
	}

	private boolean comecouSemanaPassada(Leilao leilao) {
		return diasEntre(leilao.getData(), Calendar.getInstance()) >= 7;
	}

	private int diasEntre(Calendar inicio, Calendar fim) {
		Calendar data = (Calendar) inicio.clone();
		int diasNoIntervalo = 0;
		while (data.before(fim)) {
			data.add(Calendar.DAY_OF_MONTH, 1);
			diasNoIntervalo++;
		}

		return diasNoIntervalo;
	}

	public int getTotalEncerrados() {
		return total;
	}
}
