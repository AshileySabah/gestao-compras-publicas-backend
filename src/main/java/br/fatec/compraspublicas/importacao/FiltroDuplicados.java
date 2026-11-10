package br.fatec.compraspublicas.importacao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import br.fatec.compraspublicas.dto.LinhaRejeitada;

@Component
public class FiltroDuplicados {

	private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public record Resultado(List<LinhaCompras> mantidas, List<LinhaRejeitada> rejeitadas) {
	}

	public Resultado filtrar(List<LinhaCompras> linhasValidas, Set<ChaveContrato> chavesJaCadastradas) {
		List<LinhaRejeitada> rejeitadas = new ArrayList<>();
		Map<ChaveContrato, List<LinhaCompras>> versoesPorChave = new LinkedHashMap<>();
		Map<Map<String, String>, Long> primeiraOcorrencia = new HashMap<>();

		for (LinhaCompras linha : linhasValidas) {
			Long linhaIgual = primeiraOcorrencia.putIfAbsent(linha.origem().campos(), linha.origem().numero());
			if (linhaIgual != null) {
				rejeitadas.add(rejeitar(linha, MotivoRejeicao.DUPLICADA_NO_ARQUIVO, "Igual à linha " + linhaIgual));
				continue;
			}
			versoesPorChave.computeIfAbsent(chaveDe(linha), ignorada -> new ArrayList<>()).add(linha);
		}

		List<LinhaCompras> mantidas = new ArrayList<>();
		versoesPorChave.forEach((chave, versoes) -> {
			LinhaCompras maisRecente = publicacaoMaisRecente(versoes);
			for (LinhaCompras versao : versoes) {
				if (versao != maisRecente) {
					rejeitadas.add(rejeitar(versao, MotivoRejeicao.SUBSTITUIDA_POR_PUBLICACAO_MAIS_RECENTE,
							"Mantida a linha " + maisRecente.origem().numero() + ", publicada em " + formatar(maisRecente.dataPublicacaoDou())));
				}
			}
			if (chavesJaCadastradas.contains(chave)) {
				rejeitadas.add(rejeitar(maisRecente, MotivoRejeicao.JA_CADASTRADO,
						"Contrato " + chave.numeroContrato() + " da UG " + chave.codigoUg() + " já existe no banco"));
			} else {
				mantidas.add(maisRecente);
			}
		});
		return new Resultado(mantidas, rejeitadas);
	}

	public static ChaveContrato chaveDe(LinhaCompras linha) {
		return new ChaveContrato(linha.numeroContrato(), linha.codigoUg());
	}

	private LinhaCompras publicacaoMaisRecente(List<LinhaCompras> versoes) {
		LinhaCompras escolhida = versoes.get(0);
		for (LinhaCompras versao : versoes) {
			if (!dataOuMinima(versao.dataPublicacaoDou()).isBefore(dataOuMinima(escolhida.dataPublicacaoDou()))) {
				escolhida = versao;
			}
		}
		return escolhida;
	}

	private LocalDate dataOuMinima(LocalDate data) {
		return data == null ? LocalDate.MIN : data;
	}

	private String formatar(LocalDate data) {
		return data == null ? "data não informada" : data.format(DATA_BR);
	}

	private LinhaRejeitada rejeitar(LinhaCompras linha, MotivoRejeicao motivo, String detalhe) {
		return LinhaRejeitada.de(linha.origem().numero(), linha.numeroContrato(), motivo, detalhe);
	}
}
