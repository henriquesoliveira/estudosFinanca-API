package com.rickyOl.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rickyOl.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
