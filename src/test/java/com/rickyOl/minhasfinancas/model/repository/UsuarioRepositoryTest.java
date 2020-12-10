package com.rickyOl.minhasfinancas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rickyOl.minhasfinancas.model.entity.Usuario;

@ExtendWith(SpringExtension.class)//anotation para utilizar JUnit5
@ActiveProfiles("test")// Utilizando Profile de Test apontando para H2 database
@DataJpaTest // Cria uma instancia do BD em memoria, ao finalizar, encerra a base (faz rollback)
@AutoConfigureTestDatabase(replace = Replace.NONE) //
public class UsuarioRepositoryTest {

	@Autowired
	private UsuarioRepository repository;

	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveVerificarAExistenciaDeEmail() {
		//cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		//ação
		boolean resultado = repository.existsByEmail("usuario@email.com");
		
		//verificacao
		Assertions.assertThat(resultado).isTrue();
	}
	
	@Test
	public void deveVerificarANaoExistenciaDeEmail() {
			
		//ação
		boolean resultado = repository.existsByEmail("usuario@email.com");
		
		//verificacao
		Assertions.assertThat(resultado).isFalse();
	}
	
	@Test
	public void devePersistirUsuarioNoBanco() {
		//cenario
		Usuario usuario = criarUsuario();
		//ação
		Usuario usuarioSalvo = repository.save(usuario);
		//verificacao
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUsuarioPorEmail() {
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		Optional<Usuario> usuarioReturn = repository.findByEmail(usuario.getEmail());
		
		Assertions.assertThat(usuarioReturn.isPresent()).isTrue();
		
	}
	
	@Test
	public void deveRetornarVazioBuscaDeUsuarioPorEmail() {
		Optional<Usuario> usuarioReturn = repository.findByEmail("user@test.com");
		Assertions.assertThat(usuarioReturn.isPresent()).isFalse();
	}
	
	
	private Usuario criarUsuario() {
		Usuario usuario = Usuario.builder().nome("usuario").email("usuario@email.com").senha("teste").build();
		return usuario;
	}
	
	
}
