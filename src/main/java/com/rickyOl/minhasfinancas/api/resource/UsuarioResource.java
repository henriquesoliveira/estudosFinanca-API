package com.rickyOl.minhasfinancas.api.resource;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rickyOl.minhasfinancas.api.dto.UsuarioDTO;
import com.rickyOl.minhasfinancas.exceptions.ErroAutenticacao;
import com.rickyOl.minhasfinancas.exceptions.RegraNegocioException;
import com.rickyOl.minhasfinancas.model.entity.Usuario;
import com.rickyOl.minhasfinancas.model.repository.LancamentoRepository;
import com.rickyOl.minhasfinancas.service.LancamentoService;
import com.rickyOl.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/usuario")
@RequiredArgsConstructor
public class UsuarioResource {
	
	private final UsuarioService service;
	private final LancamentoService lancamentoService;
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
		try {
			Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok(usuarioAutenticado);
		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
		Usuario usuario = Usuario.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.senha(dto.getSenha()).build();
		try {
			Usuario user = service.salvarUsuario(usuario);
			return new ResponseEntity(user, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
		Usuario usuarioBusca = service.buscarUsuarioPorId(id);
		if (Objects.nonNull(usuarioBusca)) {
			BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
			return ResponseEntity.ok(saldo);
		} else {
			return ResponseEntity.badRequest().body("Usuario não econtrado");
		}
	}
	
}
