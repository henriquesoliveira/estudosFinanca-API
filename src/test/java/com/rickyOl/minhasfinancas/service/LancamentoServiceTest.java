package com.rickyOl.minhasfinancas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.annotation.JsonTypeInfo.None;
import com.rickyOl.minhasfinancas.exceptions.RegraNegocioException;
import com.rickyOl.minhasfinancas.model.entity.Lancamento;
import com.rickyOl.minhasfinancas.model.entity.Usuario;
import com.rickyOl.minhasfinancas.model.enums.StatusLancamento;
import com.rickyOl.minhasfinancas.model.repository.LancamentoRepository;
import com.rickyOl.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.rickyOl.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;

	@Test
	public void deveSalvarLancamento() {
		Lancamento lancamentoAsalvar = LancamentoRepositoryTest.criarLancamento();

		Mockito.doNothing().when(service).validar(lancamentoAsalvar);

		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		Mockito.when(repository.save(lancamentoAsalvar)).thenReturn(lancamentoSalvo);

		Lancamento lancamento = service.salvar(lancamentoAsalvar);

		assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarLancamentoErroValidacao() {
		Lancamento lancamentoAsalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoAsalvar);
		
		catchThrowableOfType(() -> service.salvar(lancamentoAsalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoAsalvar);
	}
	
	@Test
	public void deveAtualizarLancamento() {
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);

		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		service.atualizar(lancamentoSalvo);
		
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
		
	}
	
	@Test
	public void deveLancarErroAoAtualizarLancamento() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveDeletarLancamento() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		lancamentoASalvar.setId(1L);
		
		service.excluir(lancamentoASalvar);
		
		Mockito.verify(repository).delete(lancamentoASalvar);
	}
	
	@Test
	public void naoDeveExcluirLancamento() {
		Lancamento lancamentoAExcluir = LancamentoRepositoryTest.criarLancamento();
		catchThrowableOfType(() -> service.excluir(lancamentoAExcluir), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).delete(lancamentoAExcluir);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveFiltrarLancamentos() {
		Lancamento lancamentoFiltro = LancamentoRepositoryTest.criarLancamento();
		lancamentoFiltro.setId(1L);
		
		List<Lancamento> lancamentos = Arrays.asList(lancamentoFiltro);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lancamentos);
		
		List<Lancamento> resultado = service.buscar(lancamentoFiltro);
		
		assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamentoFiltro);
	}
	
	@Test
	public void atualizarStatus() {
		Lancamento lancamentoAttStatus = LancamentoRepositoryTest.criarLancamento();
		lancamentoAttStatus.setId(1L);
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamentoAttStatus).when(service).atualizar(lancamentoAttStatus);
		
		service.atualizarStatus(lancamentoAttStatus, novoStatus);
		
		assertThat(lancamentoAttStatus.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamentoAttStatus);
	}
	
	@Test
	public void deveObterLancamentoPorId() {
		Long id =1L; 
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		Lancamento resultado = service.buscarLancamentoPorId(id);
		
		assertThat(resultado).isNotNull();
	}
	
	@Test
	public void naoDeveObterLancamentoPorId() {
		Long id = 1L; 
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Lancamento resultado = service.buscarLancamentoPorId(id);
		
		assertThat(resultado).isNull();
	}
	
	@Test
	public void naoDeveValidarLancamento() {
		
		//SEM USUARIO -> INFORMACAO OBRIGATORIA
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		catchThrowableOfType(() -> service.validar(lancamento), RegraNegocioException.class);
	}
	
	@Test
	public void deveValidarLancamento() {
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setUsuario(new Usuario(1L,"teste","teste@teste","123"));
		assertAll(() -> service.validar(lancamento));
	}
}
