package br.fatec.compraspublicas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.fatec.compraspublicas.model.Orgao;

public interface OrgaoRepository extends JpaRepository<Orgao, Long> {

	Optional<Orgao> findByCodigo(String codigo);

	List<Orgao> findAllByOrderByNomeAscCodigoAsc();
}
