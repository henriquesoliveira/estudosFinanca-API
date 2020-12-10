package com.rickyOl.minhasfinancas.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rickyOl.minhasfinancas.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	/**
	 * QueryMethods: Não é necessário dizer como montar o SQL
	 * Ele realiza a busca do parametro atraves do "By, no caso, Email da Entidade Usuario
	 * Possivel Concatenar, adicionando And
	 * findByEmailAndNome
	 * Nome exatamente igual da Entidade
	 * Optional<Usuario> findByEmail(String email);
	 */
	
	boolean existsByEmail(String email);
	
	Optional<Usuario> findByEmail(String email);
	
}
