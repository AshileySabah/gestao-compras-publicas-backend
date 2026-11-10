package br.fatec.compraspublicas.importacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import org.springframework.stereotype.Component;

@Component
public class ConversorDados {

	private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
	private static final String NUMERO_COM_PONTO_DECIMAL = "-?\\d+(\\.\\d+)?";

	public String texto(String valor) {
		return valor == null || valor.isBlank() ? null : valor.trim();
	}

	public BigDecimal valor(String texto, String coluna) {
		String limpo = texto(texto);
		if (limpo == null) {
			return null;
		}
		String normalizado = limpo.contains(",") ? limpo.replace(".", "").replace(",", ".") : limpo;
		if (!normalizado.matches(NUMERO_COM_PONTO_DECIMAL)) {
			throw new LinhaInvalidaException(MotivoRejeicao.VALOR_INVALIDO, coluna + " com valor \"" + limpo + "\"");
		}
		BigDecimal valor = new BigDecimal(normalizado);
		if (valor.signum() < 0) {
			throw new LinhaInvalidaException(MotivoRejeicao.VALOR_NEGATIVO, coluna + " com valor " + limpo);
		}
		return valor;
	}

	public LocalDate data(String texto, String coluna) {
		String limpo = texto(texto);
		if (limpo == null) {
			return null;
		}
		try {
			return LocalDate.parse(limpo, FORMATO_DATA);
		} catch (DateTimeParseException excecao) {
			throw new LinhaInvalidaException(MotivoRejeicao.DATA_INVALIDA, coluna + " com valor \"" + limpo + "\"");
		}
	}
}
