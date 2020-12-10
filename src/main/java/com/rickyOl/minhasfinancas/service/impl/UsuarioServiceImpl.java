package com.rickyOl.minhasfinancas.service.impl;


import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.rickyOl.minhasfinancas.exceptions.ErroAutenticacao;
import com.rickyOl.minhasfinancas.exceptions.RegraNegocioException;
import com.rickyOl.minhasfinancas.model.entity.Usuario;
import com.rickyOl.minhasfinancas.model.repository.UsuarioRepository;
import com.rickyOl.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	private UsuarioRepository usuarioRepository;

	public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
		super();
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
		
		if (!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuario não encontrado!");
		}

		if (!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida para o usuario informado!");
		}

		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return usuarioRepository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existeEmail = usuarioRepository.existsByEmail(email);
		if (existeEmail) {
			throw new RegraNegocioException("Já Existe um usuário com este email:".concat(email));
		}
	}

}
