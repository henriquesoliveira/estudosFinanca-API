package com.rickyOl.minhasfinancas.api.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LancamentoDTO {
	private Long id;
	private String descricao;
	private Integer ano;
	private Integer mes;
	private BigDecimal valor;
	private Long usuario;
	private String tipoLancamento;
	private String status;
}
