package br.fatec.compraspublicas.importacao;

public enum MotivoRejeicao {

	COLUNAS_INCOMPLETAS("Quantidade de colunas diferente do cabeçalho"),
	CAMPO_OBRIGATORIO_VAZIO("Campo obrigatório vazio"),
	VALOR_INVALIDO("Valor não numérico"),
	VALOR_NEGATIVO("Valor negativo"),
	DATA_INVALIDA("Data inválida"),
	VIGENCIA_INVALIDA("Fim da vigência anterior ao início"),
	DOCUMENTO_INVALIDO("Documento do contratado inválido"),
	DUPLICADA_NO_ARQUIVO("Duplicada no arquivo"),
	SUBSTITUIDA_POR_PUBLICACAO_MAIS_RECENTE("Substituída por publicação mais recente"),
	JA_CADASTRADO("Já cadastrado");

	private final String descricao;

	MotivoRejeicao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
