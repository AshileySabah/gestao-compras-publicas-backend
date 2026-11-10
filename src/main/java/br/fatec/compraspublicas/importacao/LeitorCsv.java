package br.fatec.compraspublicas.importacao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import br.fatec.compraspublicas.exception.ArquivoInvalidoException;

@Component
public class LeitorCsv {

	private static final Charset WINDOWS_1252 = Charset.forName("windows-1252");
	private static final CSVFormat FORMATO_PORTAL = CSVFormat.DEFAULT.builder()
			.setDelimiter(';')
			.setQuote('"')
			.setHeader()
			.setSkipHeaderRecord(true)
			.setIgnoreEmptyLines(true)
			.get();

	public List<LinhaCsv> ler(byte[] conteudo, String nomeArquivo, List<String> colunasObrigatorias) {
		try (Reader leitor = abrirComEncodingCorreto(conteudo); CSVParser parser = FORMATO_PORTAL.parse(leitor)) {
			List<String> cabecalho = parser.getHeaderNames();
			if (cabecalho.isEmpty()) {
				throw new ArquivoInvalidoException("O arquivo " + nomeArquivo + " está vazio.");
			}
			List<String> colunasFaltando = colunasObrigatorias.stream().filter(coluna -> !cabecalho.contains(coluna)).toList();
			if (!colunasFaltando.isEmpty()) {
				throw new ArquivoInvalidoException("O arquivo " + nomeArquivo + " não tem o cabeçalho esperado. Colunas ausentes: " + String.join(", ", colunasFaltando) + ".");
			}
			List<LinhaCsv> linhas = new ArrayList<>();
			for (CSVRecord registro : parser) {
				linhas.add(new LinhaCsv(registro.getRecordNumber() + 1, registro.toMap(), registro.isConsistent()));
			}
			if (linhas.isEmpty()) {
				throw new ArquivoInvalidoException("O arquivo " + nomeArquivo + " tem só o cabeçalho, sem nenhuma linha de dados.");
			}
			return linhas;
		} catch (IOException | UncheckedIOException | IllegalArgumentException | IllegalStateException excecao) {
			throw new ArquivoInvalidoException("Não foi possível ler o arquivo " + nomeArquivo + " como CSV separado por ponto e vírgula.");
		}
	}

	private Reader abrirComEncodingCorreto(byte[] conteudo) {
		boolean comMarcaUtf8 = conteudo.length >= 3 && (conteudo[0] & 0xFF) == 0xEF && (conteudo[1] & 0xFF) == 0xBB && (conteudo[2] & 0xFF) == 0xBF;
		if (comMarcaUtf8) {
			return new InputStreamReader(new ByteArrayInputStream(conteudo, 3, conteudo.length - 3), StandardCharsets.UTF_8);
		}
		return new InputStreamReader(new ByteArrayInputStream(conteudo), WINDOWS_1252);
	}
}
