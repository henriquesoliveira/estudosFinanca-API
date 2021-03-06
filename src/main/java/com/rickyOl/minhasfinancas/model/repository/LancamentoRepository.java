package com.rickyOl.minhasfinancas.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rickyOl.minhasfinancas.model.entity.Lancamento;
import com.rickyOl.minhasfinancas.model.enums.StatusLancamento;
import com.rickyOl.minhasfinancas.model.enums.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

	@Query(value = "select SUM(l.valor) from Lancamento l join l.usuario u"
			+ "     where u.id = :idUsuario and l.tipoLancamento =:tipoLancamento and l.status =:status group by u")
	BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus(
			@Param("idUsuario") Long idUsuario, 
			@Param("tipoLancamento") TipoLancamento tipoLancamento,
			@Param("status") StatusLancamento status);
}
