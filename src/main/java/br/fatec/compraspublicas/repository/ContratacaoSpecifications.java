package br.fatec.compraspublicas.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import br.fatec.compraspublicas.dto.FiltroContratacao;
import br.fatec.compraspublicas.model.Contratacao;
import br.fatec.compraspublicas.model.Modalidade;

public final class ContratacaoSpecifications {

	private ContratacaoSpecifications() {
	}

	public static Specification<Contratacao> comFiltros(FiltroContratacao filtro) {
		Specification<Contratacao> especificacao = semRestricao();
		if (filtro.orgaoId() != null) {
			especificacao = especificacao.and(doOrgao(filtro.orgaoId()));
		}
		if (filtro.dataInicio() != null) {
			especificacao = especificacao.and(assinadaAPartirDe(filtro.dataInicio()));
		}
		if (filtro.dataFim() != null) {
			especificacao = especificacao.and(assinadaAte(filtro.dataFim()));
		}
		if (filtro.modalidade() != null) {
			especificacao = especificacao.and(daModalidade(filtro.modalidade()));
		}
		if (filtro.valorMinimo() != null) {
			especificacao = especificacao.and(valorFinalMaiorOuIgualA(filtro.valorMinimo()));
		}
		if (filtro.valorMaximo() != null) {
			especificacao = especificacao.and(valorFinalMenorOuIgualA(filtro.valorMaximo()));
		}
		if (filtro.fornecedor() != null && !filtro.fornecedor().isBlank()) {
			especificacao = especificacao.and(fornecedorComNomeContendo(filtro.fornecedor().trim()));
		}
		return especificacao;
	}

	private static Specification<Contratacao> semRestricao() {
		return (raiz, consulta, criterios) -> criterios.conjunction();
	}

	private static Specification<Contratacao> doOrgao(Long orgaoId) {
		return (raiz, consulta, criterios) -> criterios.equal(raiz.get("orgao").get("id"), orgaoId);
	}

	private static Specification<Contratacao> assinadaAPartirDe(LocalDate data) {
		return (raiz, consulta, criterios) -> criterios.greaterThanOrEqualTo(raiz.get("dataAssinatura"), data);
	}

	private static Specification<Contratacao> assinadaAte(LocalDate data) {
		return (raiz, consulta, criterios) -> criterios.lessThanOrEqualTo(raiz.get("dataAssinatura"), data);
	}

	private static Specification<Contratacao> daModalidade(Modalidade modalidade) {
		return (raiz, consulta, criterios) -> criterios.equal(raiz.get("modalidade"), modalidade);
	}

	private static Specification<Contratacao> valorFinalMaiorOuIgualA(BigDecimal valor) {
		return (raiz, consulta, criterios) -> criterios.greaterThanOrEqualTo(raiz.get("valorFinal"), valor);
	}

	private static Specification<Contratacao> valorFinalMenorOuIgualA(BigDecimal valor) {
		return (raiz, consulta, criterios) -> criterios.lessThanOrEqualTo(raiz.get("valorFinal"), valor);
	}

	private static Specification<Contratacao> fornecedorComNomeContendo(String trecho) {
		return (raiz, consulta, criterios) -> criterios.like(
				criterios.lower(raiz.get("fornecedor").get("nome")), "%" + trecho.toLowerCase() + "%");
	}
}
