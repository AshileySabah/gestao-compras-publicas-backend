package br.fatec.compraspublicas.dto;

import br.fatec.compraspublicas.importacao.MotivoRejeicao;

public record LinhaRejeitada(long linha, String numeroContrato, String motivo, String detalhe) {

	public static LinhaRejeitada de(long linha, String numeroContrato, MotivoRejeicao motivo, String detalhe) {
		return new LinhaRejeitada(linha, numeroContrato, motivo.getDescricao(), detalhe);
	}
}
