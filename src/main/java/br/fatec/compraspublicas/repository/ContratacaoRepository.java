package br.fatec.compraspublicas.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import br.fatec.compraspublicas.importacao.ChaveContrato;
import br.fatec.compraspublicas.model.Contratacao;

public interface ContratacaoRepository extends JpaRepository<Contratacao, Long>, JpaSpecificationExecutor<Contratacao> {

	@Override
	@EntityGraph(attributePaths = { "orgao", "fornecedor" })
	Page<Contratacao> findAll(Specification<Contratacao> especificacao, Pageable paginacao);

	@Query("select new br.fatec.compraspublicas.importacao.ChaveContrato(c.numeroContrato, c.codigoUg) from Contratacao c")
	List<ChaveContrato> buscarTodasAsChaves();

	boolean existsByNumeroContratoAndCodigoUg(String numeroContrato, String codigoUg);

	boolean existsByNumeroContratoAndCodigoUgAndIdNot(String numeroContrato, String codigoUg, Long id);
}
