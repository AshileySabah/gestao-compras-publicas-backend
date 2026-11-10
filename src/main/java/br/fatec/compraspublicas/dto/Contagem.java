package br.fatec.compraspublicas.dto;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Contagem(String descricao, long quantidade) {

	public static <T> List<Contagem> agrupar(List<T> itens, Function<T, String> descricao) {
		return itens.stream()
				.collect(Collectors.groupingBy(descricao, Collectors.counting()))
				.entrySet().stream()
				.map(entrada -> new Contagem(entrada.getKey(), entrada.getValue()))
				.sorted(Comparator.comparingLong(Contagem::quantidade).reversed())
				.toList();
	}
}
