package com.rickyOl.minhasfinancas.api.resource;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rickyOl.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.rickyOl.minhasfinancas.api.dto.LancamentoDTO;
import com.rickyOl.minhasfinancas.exceptions.RegraNegocioException;
import com.rickyOl.minhasfinancas.model.entity.Lancamento;
import com.rickyOl.minhasfinancas.model.entity.Usuario;
import com.rickyOl.minhasfinancas.model.enums.StatusLancamento;
import com.rickyOl.minhasfinancas.model.enums.TipoLancamento;
import com.rickyOl.minhasfinancas.service.LancamentoService;
import com.rickyOl.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor // Seja criado um construtor com todos argumentos obrigatórios 
public class LancamentoResource {

	private final LancamentoService service;
	private final UsuarioService usuarioService;

	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamento lancamento = montaObj(dto);
			lancamento = service.salvar(lancamento);
			return new ResponseEntity(lancamento, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		try {
			Lancamento lancamentoEncontrado = service.buscarLancamentoPorId(id);

			if (Objects.nonNull(lancamentoEncontrado)) {
				Lancamento lancamento = montaObj(dto);
				lancamento.setId(lancamentoEncontrado.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} else {
				throw new RegraNegocioException("Lancamento não encontrado");
			}
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}/atualizaStatus")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO status) {
		Lancamento lancamentoEncontrado = service.buscarLancamentoPorId(id);
		if (Objects.nonNull(lancamentoEncontrado)) {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(status.getStatus());
			
			if(Objects.isNull(statusSelecionado)) {
				return ResponseEntity.badRequest().body("informe um Status Válido");
			}else {
				service.atualizarStatus(lancamentoEncontrado, statusSelecionado);
				return ResponseEntity.ok(lancamentoEncontrado);
			}
		}else {
			return ResponseEntity.badRequest().body("Lançamento não encontrado!");
		}
	}

	@DeleteMapping("{id}")
	public ResponseEntity excluir(@PathVariable("id") Long id) {
		Lancamento lancamentoEncontrado = service.buscarLancamentoPorId(id);

		if (Objects.nonNull(lancamentoEncontrado)) {
			service.excluir(lancamentoEncontrado);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity("Lancamento não encontrado", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping
	public ResponseEntity buscarLancamentos(@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam(value = "tipoLancamento", required = false) String tipoLancamento,
			@RequestParam("usuario") Long usuario) {

		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		lancamentoFiltro.setTipoLancamento(Objects.nonNull(tipoLancamento) ? TipoLancamento.valueOf(tipoLancamento) : null);
		try {
			Usuario user = usuarioService.buscarUsuarioPorId(usuario);
			if (Objects.isNull(user)) {
				throw new RegraNegocioException("Usuario informado não encontrado");
			} else {
				lancamentoFiltro.setUsuario(user);
				List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
				return ResponseEntity.ok(lancamentos);
			}

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	private Lancamento montaObj(LancamentoDTO dto) {

		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		lancamento.setTipoLancamento(TipoLancamento.valueOf(dto.getTipoLancamento()));
		lancamento.setStatus(Objects.nonNull(dto.getStatus()) ? StatusLancamento.valueOf(dto.getStatus()): null );
		lancamento.setUsuario(usuarioService.buscarUsuarioPorId(dto.getUsuario()));

		if (Objects.isNull(lancamento.getUsuario())) {
			throw new RegraNegocioException("Usuário não encontrado para o ID informado");
		}
		return lancamento;
	}

}
