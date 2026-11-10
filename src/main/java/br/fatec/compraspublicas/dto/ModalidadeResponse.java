package br.fatec.compraspublicas.dto;

import java.util.Arrays;
import java.util.List;

import br.fatec.compraspublicas.model.Modalidade;

public record ModalidadeResponse(Modalidade valor, String codigo, String descricao) {

	public static List<ModalidadeResponse> todas() {
		return Arrays.stream(Modalidade.values()).map(ModalidadeResponse::de).toList();
	}

	public static ModalidadeResponse de(Modalidade modalidade) {
		return new ModalidadeResponse(modalidade, modalidade.getCodigo(), modalidade.getDescricao());
	}
}
