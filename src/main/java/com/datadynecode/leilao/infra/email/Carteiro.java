package com.datadynecode.leilao.infra.email;

import com.datadynecode.leilao.dominio.Leilao;

public interface Carteiro {
    void envia(Leilao leilao);
}
