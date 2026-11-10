package br.fatec.compraspublicas.dto;

import br.fatec.compraspublicas.model.Modalidade;

public record ModalidadeResponse(Modalidade valor, String codigo, String descricao) {

	public static ModalidadeResponse de(Modalidade modalidade) {
		return new ModalidadeResponse(modalidade, modalidade.getCodigo(), modalidade.getDescricao());
	}
}
