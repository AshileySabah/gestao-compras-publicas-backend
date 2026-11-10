package br.fatec.compraspublicas.importacao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import br.fatec.compraspublicas.model.Modalidade;

@Component
public class ModalidadePorItens {

	private static final int TAMANHO_CODIGO_ITEM = 22;
	private static final int INICIO_MODALIDADE = 6;
	private static final int FIM_MODALIDADE = 8;

	public Map<ChaveContrato, Modalidade> mapear(List<LinhaCsv> itens) {
		Map<ChaveContrato, Set<Modalidade>> modalidadesEncontradas = new HashMap<>();
		for (LinhaCsv item : itens) {
			modalidadeDoCodigo(item.valor(ColunasCsv.ITEM_CODIGO_ITEM_COMPRA)).ifPresent(modalidade -> {
				ChaveContrato chave = new ChaveContrato(item.valor(ColunasCsv.ITEM_NUMERO_CONTRATO), item.valor(ColunasCsv.ITEM_CODIGO_UG));
				modalidadesEncontradas.computeIfAbsent(chave, ignorada -> new HashSet<>()).add(modalidade);
			});
		}
		Map<ChaveContrato, Modalidade> modalidadePorContrato = new HashMap<>();
		modalidadesEncontradas.forEach((chave, modalidades) -> {
			if (modalidades.size() == 1) {
				modalidadePorContrato.put(chave, modalidades.iterator().next());
			}
		});
		return modalidadePorContrato;
	}

	private Optional<Modalidade> modalidadeDoCodigo(String codigoItem) {
		if (codigoItem.length() != TAMANHO_CODIGO_ITEM || !codigoItem.chars().allMatch(Character::isDigit)) {
			return Optional.empty();
		}
		return Modalidade.deCodigo(codigoItem.substring(INICIO_MODALIDADE, FIM_MODALIDADE));
	}
}
