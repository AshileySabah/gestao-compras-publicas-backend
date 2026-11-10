package br.fatec.compraspublicas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FornecedorRequest(
		@NotBlank(message = "Informe o documento do fornecedor.") @Size(max = 20, message = "O documento do fornecedor deve ter no máximo 20 caracteres.") String documento,
		@NotBlank(message = "Informe o nome do fornecedor.") @Size(max = 255, message = "O nome do fornecedor deve ter no máximo 255 caracteres.") String nome) {
}
