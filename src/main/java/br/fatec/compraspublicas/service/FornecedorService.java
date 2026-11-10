package br.fatec.compraspublicas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.fatec.compraspublicas.dto.FornecedorResponse;
import br.fatec.compraspublicas.model.Fornecedor;
import br.fatec.compraspublicas.repository.FornecedorRepository;

@Service
public class FornecedorService {

	private final FornecedorRepository fornecedorRepository;

	public FornecedorService(FornecedorRepository fornecedorRepository) {
		this.fornecedorRepository = fornecedorRepository;
	}

	@Transactional(readOnly = true)
	public List<FornecedorResponse> buscar(String termo) {
		List<Fornecedor> encontrados = termo == null || termo.isBlank()
				? fornecedorRepository.findTop20ByOrderByNomeAsc()
				: fornecedorRepository.findTop20ByNomeContainingIgnoreCaseOrDocumentoContainingOrderByNomeAsc(termo.trim(), termo.trim());
		return encontrados.stream().map(FornecedorResponse::de).toList();
	}
}
