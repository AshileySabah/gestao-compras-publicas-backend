package br.fatec.compraspublicas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.fatec.compraspublicas.model.Contratacao;
import br.fatec.compraspublicas.model.OrigemModalidade;
import br.fatec.compraspublicas.model.OrigemRegistro;

public record ContratacaoResponse(
		Long id,
		String numeroContrato,
		String codigoUg,
		String nomeUg,
		String objeto,
		ModalidadeResponse modalidade,
		OrigemModalidade origemModalidade,
		LocalDate dataAssinatura,
		LocalDate dataPublicacaoDou,
		LocalDate dataInicioVigencia,
		LocalDate dataFimVigencia,
		BigDecimal valorInicial,
		BigDecimal valorFinal,
		String numeroLicitacao,
		OrigemRegistro origemRegistro,
		OrgaoResponse orgao,
		FornecedorResponse fornecedor) {

	public static ContratacaoResponse de(Contratacao contratacao) {
		return new ContratacaoResponse(
				contratacao.getId(),
				contratacao.getNumeroContrato(),
				contratacao.getCodigoUg(),
				contratacao.getNomeUg(),
				contratacao.getObjeto(),
				ModalidadeResponse.de(contratacao.getModalidade()),
				contratacao.getOrigemModalidade(),
				contratacao.getDataAssinatura(),
				contratacao.getDataPublicacaoDou(),
				contratacao.getDataInicioVigencia(),
				contratacao.getDataFimVigencia(),
				contratacao.getValorInicial(),
				contratacao.getValorFinal(),
				contratacao.getNumeroLicitacao(),
				contratacao.getOrigemRegistro(),
				OrgaoResponse.de(contratacao.getOrgao()),
				FornecedorResponse.de(contratacao.getFornecedor()));
	}
}
