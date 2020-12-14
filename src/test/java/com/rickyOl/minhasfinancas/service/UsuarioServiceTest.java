package com.rickyOl.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rickyOl.minhasfinancas.exceptions.ErroAutenticacao;
import com.rickyOl.minhasfinancas.exceptions.RegraNegocioException;
import com.rickyOl.minhasfinancas.model.entity.Usuario;
import com.rickyOl.minhasfinancas.model.repository.UsuarioRepository;
import com.rickyOl.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@MockBean
	UsuarioRepository repository;
	
	@SpyBean
	UsuarioServiceImpl service;
	
	
	@Test
	public void deveValidarEmailExistente() {
		//Cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		//ação
		assertAll(()->service.validarEmail("email@email.com"));
	}
	
	@Test
	public void deveValidarEmailNaoExistente(){
		//Cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//ação
		assertThrows(RegraNegocioException.class,()->
		service.validarEmail("usuario@email.com"));
	}
	
	@Test
	public void deveAutenticarComSucesso() {
		
		String email = "teste@hotmail.com";
		String senha = "123";
		Usuario usuario = Usuario.builder().email(email).senha(senha).build();
		
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		Usuario result = service.autenticar(email, senha);
		
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test
	public void erroUsuarioNaoEncontrado() {
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		Throwable exception = Assertions.catchThrowable(()->
		service.autenticar("email@email.com", "321"));

		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuario não encontrado!");
	}
	
	@Test
	public void erroSenhaUsuarioInvalida() {
		String senha = "123";
		Usuario usuario = Usuario.builder().email("").senha(senha).build();
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		Throwable exception = Assertions.catchThrowable(()->service.autenticar("teste@hotmail.com", "321"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida para o usuario informado!");
		
	}
	
	@Test
	public void salvarUsuarioSucesso() {
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		
		Usuario usuario = Usuario.builder().nome("nome").senha("senha").email("email").build();
		usuario.setId(1L);
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}
	
	@Test
	public void naoSalvaUsuarioFalhaEmail() {
		String email = "email";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		Mockito.verify(repository, Mockito.never()).save(usuario);
	}
	
}
