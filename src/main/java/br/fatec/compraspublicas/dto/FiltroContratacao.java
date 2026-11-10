package br.fatec.compraspublicas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import br.fatec.compraspublicas.model.Modalidade;

public record FiltroContratacao(
		Long orgaoId,
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
		Modalidade modalidade,
		BigDecimal valorMinimo,
		BigDecimal valorMaximo,
		String fornecedor) {
}
