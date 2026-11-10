package br.fatec.compraspublicas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrgaoRequest(
		@NotBlank(message = "Informe o código do órgão.") @Size(max = 20, message = "O código do órgão deve ter no máximo 20 caracteres.") String codigo,
		@NotBlank(message = "Informe o nome do órgão.") @Size(max = 255, message = "O nome do órgão deve ter no máximo 255 caracteres.") String nome,
		@Size(max = 20, message = "O código do órgão superior deve ter no máximo 20 caracteres.") String codigoOrgaoSuperior,
		@Size(max = 255, message = "O nome do órgão superior deve ter no máximo 255 caracteres.") String nomeOrgaoSuperior) {
}
