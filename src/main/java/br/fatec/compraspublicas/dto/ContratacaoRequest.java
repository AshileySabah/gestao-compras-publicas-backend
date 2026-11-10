package br.fatec.compraspublicas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.fatec.compraspublicas.model.Modalidade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ContratacaoRequest(
		@NotBlank(message = "Informe o número do contrato.") @Size(max = 30, message = "O número do contrato deve ter no máximo 30 caracteres.") String numeroContrato,
		@NotBlank(message = "Informe o código da UG.") @Size(max = 20, message = "O código da UG deve ter no máximo 20 caracteres.") String codigoUg,
		@Size(max = 255, message = "O nome da UG deve ter no máximo 255 caracteres.") String nomeUg,
		String objeto,
		Modalidade modalidade,
		@NotNull(message = "Informe a data de assinatura.") LocalDate dataAssinatura,
		LocalDate dataPublicacaoDou,
		LocalDate dataInicioVigencia,
		LocalDate dataFimVigencia,
		@PositiveOrZero(message = "O valor inicial não pode ser negativo.") BigDecimal valorInicial,
		@NotNull(message = "Informe o valor final.") @PositiveOrZero(message = "O valor final não pode ser negativo.") BigDecimal valorFinal,
		@Size(max = 30, message = "O número da licitação deve ter no máximo 30 caracteres.") String numeroLicitacao,
		Long orgaoId,
		@Valid OrgaoRequest novoOrgao,
		Long fornecedorId,
		@Valid FornecedorRequest novoFornecedor) {
}
