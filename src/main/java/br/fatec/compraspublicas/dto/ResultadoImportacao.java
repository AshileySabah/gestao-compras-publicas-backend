package br.fatec.compraspublicas.dto;

import java.util.List;

public record ResultadoImportacao(
		int linhasLidas,
		int importadas,
		int rejeitadas,
		long tempoProcessamentoMs,
		boolean arquivoItensEnviado,
		List<Contagem> rejeicoesPorMotivo,
		List<Contagem> modalidadesPorOrigem,
		List<LinhaRejeitada> linhasRejeitadas) {
}
