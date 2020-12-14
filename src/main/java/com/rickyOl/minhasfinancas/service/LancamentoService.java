package com.rickyOl.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.List;

import com.rickyOl.minhasfinancas.model.entity.Lancamento;
import com.rickyOl.minhasfinancas.model.enums.StatusLancamento;

public interface LancamentoService {
	
	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void excluir(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamento);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
	
	Lancamento buscarLancamentoPorId(Long id);

	BigDecimal obterSaldoPorUsuario(Long id);
}
