package com.datadynecode.leilao.infra.dao;

import com.datadynecode.leilao.dominio.Pagamento;

public interface PagamentosDao {

    void salva(Pagamento pagamento);
}
