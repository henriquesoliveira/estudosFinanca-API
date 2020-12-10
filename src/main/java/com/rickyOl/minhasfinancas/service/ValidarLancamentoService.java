package com.rickyOl.minhasfinancas.service;

import com.rickyOl.minhasfinancas.exceptions.RegraNegocioException;
import com.rickyOl.minhasfinancas.model.entity.Lancamento;

public interface ValidarLancamentoService {
	
	void validarLancamento(Lancamento lancamento) throws RegraNegocioException;

}
