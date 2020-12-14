package com.rickyOl.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.rickyOl.minhasfinancas.exceptions.RegraNegocioException;
import com.rickyOl.minhasfinancas.model.entity.Lancamento;
import com.rickyOl.minhasfinancas.service.ValidarLancamentoService;

@Service
public class ValidarLancamentoServiceImpl implements ValidarLancamentoService {

	@Override
	public void validarLancamento(Lancamento lancamento) throws RegraNegocioException {
		
		if(Objects.isNull(lancamento.getDescricao()) || StringUtils.isBlank(lancamento.getDescricao())){
			throw new RegraNegocioException("Informe uma Descrição válida");
		}
		
		if(Objects.isNull(lancamento.getMes()) || (lancamento.getMes() < 1 || lancamento.getMes() > 12)) {
			throw new RegraNegocioException("Informe um Mês válido");
		}
		
		if(Objects.isNull(lancamento.getAno()) || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um Ano válido");
		}
		
		if(Objects.isNull(lancamento.getUsuario()) || Objects.isNull(lancamento.getUsuario().getId())) {
			throw new RegraNegocioException("Informe um Usuário válido");
		}
		
		if(Objects.isNull(lancamento.getValor()) || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1 ) {
			throw new RegraNegocioException("Informe um Valor válido");
		}
		
		if(Objects.isNull(lancamento.getTipoLancamento())) {
			throw new RegraNegocioException("Informe um Tipo de lançamento válido");
		}
	}

}
