package br.fatec.compraspublicas.importacao;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.fatec.compraspublicas.model.Modalidade;
import br.fatec.compraspublicas.model.OrigemModalidade;

@Component
public class ResolvedorModalidade {

	public ModalidadeResolvida resolver(LinhaCompras linha, Map<ChaveContrato, Modalidade> modalidadePorItens) {
		Optional<Modalidade> doArquivo = Modalidade.deTexto(linha.modalidadeCompra())
				.or(() -> Modalidade.deTexto(linha.modalidadeCompraLicitacao()));
		if (doArquivo.isPresent()) {
			return new ModalidadeResolvida(doArquivo.get(), OrigemModalidade.ARQUIVO_COMPRAS);
		}
		Modalidade dosItens = modalidadePorItens.get(new ChaveContrato(linha.numeroContrato(), linha.codigoUg()));
		if (dosItens != null) {
			return new ModalidadeResolvida(dosItens, OrigemModalidade.DERIVADA_ITEM);
		}
		return new ModalidadeResolvida(Modalidade.NAO_INFORMADA, OrigemModalidade.NAO_INFORMADA);
	}
}
