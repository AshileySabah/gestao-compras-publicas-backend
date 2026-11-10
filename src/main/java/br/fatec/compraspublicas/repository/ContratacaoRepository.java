package br.fatec.compraspublicas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.fatec.compraspublicas.model.Contratacao;

public interface ContratacaoRepository extends JpaRepository<Contratacao, Long> {

	@Override
	@EntityGraph(attributePaths = { "orgao", "fornecedor" })
	Page<Contratacao> findAll(Pageable pageable);

	boolean existsByNumeroContratoAndCodigoUg(String numeroContrato, String codigoUg);

	boolean existsByNumeroContratoAndCodigoUgAndIdNot(String numeroContrato, String codigoUg, Long id);
}
