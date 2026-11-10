package br.fatec.compraspublicas.dto;

import br.fatec.compraspublicas.model.Fornecedor;
import br.fatec.compraspublicas.model.TipoFornecedor;

public record FornecedorResponse(Long id, String documento, String nome, TipoFornecedor tipo) {

	public static FornecedorResponse de(Fornecedor fornecedor) {
		return new FornecedorResponse(fornecedor.getId(), fornecedor.getDocumento(), fornecedor.getNome(), fornecedor.getTipo());
	}
}
