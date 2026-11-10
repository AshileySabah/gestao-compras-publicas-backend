package br.fatec.compraspublicas.dto;

import br.fatec.compraspublicas.model.Modalidade;

public record QuantidadePorModalidade(Modalidade modalidade, String descricao, long quantidade) {

	public QuantidadePorModalidade(Modalidade modalidade, Long quantidade) {
		this(modalidade, modalidade.getDescricao(), quantidade);
	}
}
