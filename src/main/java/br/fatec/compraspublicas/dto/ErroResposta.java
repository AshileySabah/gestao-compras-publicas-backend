package br.fatec.compraspublicas.dto;

import java.util.List;

public record ErroResposta(int status, String mensagem, List<Campo> campos) {

	public record Campo(String campo, String mensagem) {
	}

	public ErroResposta(int status, String mensagem) {
		this(status, mensagem, List.of());
	}
}
