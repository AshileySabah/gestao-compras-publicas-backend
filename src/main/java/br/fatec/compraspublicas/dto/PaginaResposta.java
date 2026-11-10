package br.fatec.compraspublicas.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record PaginaResposta<T>(List<T> conteudo, int pagina, int tamanho, long totalElementos, int totalPaginas) {

	public static <T> PaginaResposta<T> de(Page<T> pagina) {
		return new PaginaResposta<>(pagina.getContent(), pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(), pagina.getTotalPages());
	}
}
