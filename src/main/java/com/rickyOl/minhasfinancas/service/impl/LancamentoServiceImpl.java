package com.rickyOl.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rickyOl.minhasfinancas.exceptions.RegraNegocioException;
import com.rickyOl.minhasfinancas.model.entity.Lancamento;
import com.rickyOl.minhasfinancas.model.enums.StatusLancamento;
import com.rickyOl.minhasfinancas.model.enums.TipoLancamento;
import com.rickyOl.minhasfinancas.model.repository.LancamentoRepository;
import com.rickyOl.minhasfinancas.service.LancamentoService;
import com.rickyOl.minhasfinancas.service.ValidarLancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {
	
	private LancamentoRepository repository;
	
	public LancamentoServiceImpl(LancamentoRepository repository) {
		this.repository = repository;
	}
	
//	@Autowired
//	private ValidarLancamentoService validarService;

	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		validar(lancamento);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public void excluir(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		repository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamento) {
		Example example = Example.of(lancamento, ExampleMatcher.matching()
				.withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING));
		return repository.findAll(example);
	}

	@Override
	@Transactional
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		//validarService.validarLancamento(lancamento); //TODO: refat
		
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

	@Override
	public Lancamento buscarLancamentoPorId(Long id) {
		Optional<Lancamento> lancamento = repository.findById(id);
		return lancamento.isPresent() ? lancamento.get() : null;
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obterSaldoPorUsuario(Long id) {
		
		BigDecimal receita =  repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.RECEITA);
		BigDecimal despesa =  repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.DESPESA);
		
		if (Objects.isNull(receita)) {
			receita = BigDecimal.ZERO;
		}
		
		if(Objects.isNull(despesa)) {
			despesa = BigDecimal.ZERO;
		}
		
		return receita.subtract(despesa);
	}

}
