package br.fatec.compraspublicas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.fatec.compraspublicas.model.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

	Optional<Fornecedor> findByDocumento(String documento);
}
