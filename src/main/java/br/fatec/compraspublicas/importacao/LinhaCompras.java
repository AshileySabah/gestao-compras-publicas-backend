package br.fatec.compraspublicas.importacao;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.fatec.compraspublicas.model.TipoFornecedor;

public record LinhaCompras(
		LinhaCsv origem,
		String numeroContrato,
		String codigoUg,
		String nomeUg,
		String objeto,
		String modalidadeCompra,
		String modalidadeCompraLicitacao,
		String codigoOrgaoSuperior,
		String nomeOrgaoSuperior,
		String codigoOrgao,
		String nomeOrgao,
		LocalDate dataAssinatura,
		LocalDate dataPublicacaoDou,
		LocalDate dataInicioVigencia,
		LocalDate dataFimVigencia,
		String documentoContratado,
		String nomeContratado,
		TipoFornecedor tipoFornecedor,
		BigDecimal valorInicial,
		BigDecimal valorFinal,
		String numeroLicitacao) {
}
