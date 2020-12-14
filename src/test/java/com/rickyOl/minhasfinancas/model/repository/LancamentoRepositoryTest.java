package com.rickyOl.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rickyOl.minhasfinancas.model.entity.Lancamento;
import com.rickyOl.minhasfinancas.model.entity.Usuario;
import com.rickyOl.minhasfinancas.model.enums.StatusLancamento;
import com.rickyOl.minhasfinancas.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest 
@AutoConfigureTestDatabase(replace = Replace.NONE) 
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarLancamento() {
		Lancamento lancamento =criarLancamento();
		
		lancamento = repository.save(lancamento);
		
		assertThat(lancamento.getId()).isNotNull();
	}

	
	@Test
	public void excluirLancamento() {
		Lancamento lancamento = CriarPersistir();
		
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		
		repository.delete(lancamento);
		
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoInexistente).isNull();
	}
	
	@Test
	public void deveAtualizarLancamento() {
		Lancamento lancamento = CriarPersistir();
		
		lancamento.setAno(2021);
		lancamento.setDescricao("teste");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		
		repository.save(lancamento);
		
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2021);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("teste");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
	}

	@Test
	public void buscarLancamentoPorId() {
		Lancamento lancamento = CriarPersistir();
		
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}
	
	private Lancamento CriarPersistir() {
		Lancamento lancamento = criarLancamento();
		lancamento = entityManager.persist(lancamento);
		return lancamento;
	}
	
	public static Lancamento criarLancamento() {
		return Lancamento.builder()
				.ano(2020)
				.mes(1)
				.descricao("Qualquer")
				.valor(BigDecimal.valueOf(14))
				.tipoLancamento(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now()).build();
	}
	
}
