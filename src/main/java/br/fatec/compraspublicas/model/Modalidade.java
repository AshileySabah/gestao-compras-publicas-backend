package br.fatec.compraspublicas.model;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Modalidade {

	CONVITE("01", "Convite"),
	TOMADA_DE_PRECOS("02", "Tomada de Preços"),
	CONCORRENCIA("03", "Concorrência"),
	CONCORRENCIA_INTERNACIONAL("04", "Concorrência Internacional"),
	PREGAO("05", "Pregão"),
	DISPENSA_DE_LICITACAO("06", "Dispensa de Licitação"),
	INEXIGIBILIDADE_DE_LICITACAO("07", "Inexigibilidade de Licitação"),
	CONCURSO("20", "Concurso"),
	TOMADA_DE_PRECOS_POR_TECNICA_E_PRECO("22", "Tomada de Preços por Técnica e Preço"),
	CONCORRENCIA_POR_TECNICA_E_PRECO("33", "Concorrência por Técnica e Preço"),
	CONCORRENCIA_INTERNACIONAL_POR_TECNICA_E_PRECO("44", "Concorrência Internacional por Técnica e Preço"),
	PREGAO_REGISTRO_DE_PRECOS("-99", "Pregão – Registro de Preços"),
	NAO_INFORMADA(null, "Não informada");

	private final String codigo;
	private final String descricao;

	Modalidade(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public static Optional<Modalidade> deCodigo(String codigo) {
		if (codigo == null) {
			return Optional.empty();
		}
		String codigoLimpo = codigo.trim();
		return Arrays.stream(values())
				.filter(modalidade -> codigoLimpo.equals(modalidade.codigo))
				.findFirst();
	}

	public static Optional<Modalidade> deTexto(String texto) {
		if (texto == null || texto.isBlank()) {
			return Optional.empty();
		}
		String textoComparavel = paraComparacao(texto);
		return Arrays.stream(values())
				.filter(modalidade -> modalidade.codigo != null)
				.filter(modalidade -> paraComparacao(modalidade.descricao).equals(textoComparavel))
				.findFirst();
	}

	private static String paraComparacao(String texto) {
		String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		String somenteLetrasENumeros = semAcentos.toLowerCase().replaceAll("[^a-z0-9]+", " ").trim();
		return Arrays.stream(somenteLetrasENumeros.split(" "))
				.map(palavra -> palavra.length() > 1 && palavra.endsWith("s") ? palavra.substring(0, palavra.length() - 1) : palavra)
				.collect(Collectors.joining(" "));
	}
}
