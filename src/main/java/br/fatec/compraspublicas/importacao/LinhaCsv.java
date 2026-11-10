package br.fatec.compraspublicas.importacao;

import java.util.Map;

public record LinhaCsv(long numero, Map<String, String> campos, boolean completa) {

	public String valor(String coluna) {
		String valor = campos.get(coluna);
		return valor == null ? "" : valor.trim();
	}
}
