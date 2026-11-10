package br.fatec.compraspublicas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.fatec.compraspublicas.model.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

	Optional<Fornecedor> findByDocumento(String documento);

	List<Fornecedor> findTop20ByOrderByNomeAsc();

	List<Fornecedor> findTop20ByNomeContainingIgnoreCaseOrDocumentoContainingOrderByNomeAsc(String nome, String documento);
}
