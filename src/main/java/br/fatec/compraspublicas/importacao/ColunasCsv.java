package br.fatec.compraspublicas.importacao;

import java.util.List;

public final class ColunasCsv {

	public static final String NUMERO_CONTRATO = "Número do Contrato";
	public static final String OBJETO = "Objeto";
	public static final String MODALIDADE_COMPRA = "Modalidade Compra";
	public static final String CODIGO_ORGAO_SUPERIOR = "Código Órgão Superior";
	public static final String NOME_ORGAO_SUPERIOR = "Nome Órgão Superior";
	public static final String CODIGO_ORGAO = "Código Órgão";
	public static final String NOME_ORGAO = "Nome Órgão";
	public static final String CODIGO_UG = "Código UG";
	public static final String NOME_UG = "Nome UG";
	public static final String DATA_ASSINATURA = "Data Assinatura Contrato";
	public static final String DATA_PUBLICACAO_DOU = "Data Publicação DOU";
	public static final String DATA_INICIO_VIGENCIA = "Data Início Vigência";
	public static final String DATA_FIM_VIGENCIA = "Data Fim Vigência";
	public static final String CODIGO_CONTRATADO = "Código Contratado";
	public static final String NOME_CONTRATADO = "Nome Contratado";
	public static final String VALOR_INICIAL = "Valor Inicial Compra";
	public static final String VALOR_FINAL = "Valor Final Compra";
	public static final String NUMERO_LICITACAO = "Número Licitação";
	public static final String MODALIDADE_COMPRA_LICITACAO = "Modalidade Compra Licitação";

	public static final String ITEM_NUMERO_CONTRATO = "Número Contrato";
	public static final String ITEM_CODIGO_UG = "Código UG";
	public static final String ITEM_CODIGO_ITEM_COMPRA = "Código Item Compra";

	public static final List<String> OBRIGATORIAS_COMPRAS = List.of(NUMERO_CONTRATO, OBJETO, MODALIDADE_COMPRA,
			CODIGO_ORGAO_SUPERIOR, NOME_ORGAO_SUPERIOR, CODIGO_ORGAO, NOME_ORGAO, CODIGO_UG, NOME_UG, DATA_ASSINATURA,
			DATA_PUBLICACAO_DOU, DATA_INICIO_VIGENCIA, DATA_FIM_VIGENCIA, CODIGO_CONTRATADO, NOME_CONTRATADO,
			VALOR_INICIAL, VALOR_FINAL, NUMERO_LICITACAO, MODALIDADE_COMPRA_LICITACAO);

	public static final List<String> OBRIGATORIAS_ITENS = List.of(ITEM_NUMERO_CONTRATO, ITEM_CODIGO_UG, ITEM_CODIGO_ITEM_COMPRA);

	private ColunasCsv() {
	}
}
