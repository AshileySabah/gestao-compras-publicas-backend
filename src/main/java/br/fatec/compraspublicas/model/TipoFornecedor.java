package br.fatec.compraspublicas.model;

import java.util.Optional;

public enum TipoFornecedor {
	PF,
	PJ,
	ESTRANGEIRO,
	SIGILOSO;

	public static Optional<TipoFornecedor> deDocumento(String documento) {
		if (documento == null) {
			return Optional.empty();
		}
		String documentoLimpo = documento.trim();
		if (documentoLimpo.equals("-11")) {
			return Optional.of(SIGILOSO);
		}
		if (documentoLimpo.toUpperCase().startsWith("EX")) {
			return Optional.of(ESTRANGEIRO);
		}
		if (documentoLimpo.matches("\\d{11}")) {
			return Optional.of(PF);
		}
		if (documentoLimpo.matches("\\d{14}")) {
			return Optional.of(PJ);
		}
		return Optional.empty();
	}
}
