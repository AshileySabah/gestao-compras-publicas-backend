package br.fatec.compraspublicas.importacao;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.fatec.compraspublicas.exception.ArquivoInvalidoException;

@Component
public class LeitorZip {

	private static final String FINAL_ARQUIVO_COMPRAS = "_compras.csv";
	private static final String FINAL_ARQUIVO_ITENS = "_itemcompra.csv";

	public ArquivosDoZip ler(MultipartFile arquivo) {
		if (arquivo == null || arquivo.isEmpty()) {
			throw new ArquivoInvalidoException("Nenhum arquivo foi enviado ou o arquivo está vazio.");
		}
		String nome = arquivo.getOriginalFilename();
		if (nome == null || !nome.toLowerCase().endsWith(".zip")) {
			throw new ArquivoInvalidoException("Envie o arquivo .zip baixado do Portal da Transparência (por exemplo, 202606_Compras.zip). Arquivo recebido: " + nome + ".");
		}
		byte[] compras = null;
		byte[] itens = null;
		boolean possuiAlgumArquivo = false;
		try (ZipInputStream zip = new ZipInputStream(arquivo.getInputStream())) {
			ZipEntry entrada;
			while ((entrada = zip.getNextEntry()) != null) {
				possuiAlgumArquivo = true;
				String nomeEntrada = entrada.getName().toLowerCase();
				if (nomeEntrada.endsWith(FINAL_ARQUIVO_COMPRAS)) {
					compras = zip.readAllBytes();
				} else if (nomeEntrada.endsWith(FINAL_ARQUIVO_ITENS)) {
					itens = zip.readAllBytes();
				}
			}
		} catch (IOException | IllegalArgumentException excecao) {
			throw new ArquivoInvalidoException("O arquivo enviado não é um ZIP válido ou está corrompido.");
		}
		if (!possuiAlgumArquivo) {
			throw new ArquivoInvalidoException("O arquivo enviado não é um ZIP válido ou não contém nenhum arquivo.");
		}
		if (compras == null) {
			throw new ArquivoInvalidoException("O ZIP não contém o arquivo de contratos (*_Compras.csv). Confira se é o ZIP de Compras do Portal da Transparência.");
		}
		return new ArquivosDoZip(compras, itens);
	}
}
