package br.fatec.compraspublicas.controller;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.fatec.compraspublicas.dto.ContratacaoRequest;
import br.fatec.compraspublicas.dto.ContratacaoResponse;
import br.fatec.compraspublicas.dto.PaginaResposta;
import br.fatec.compraspublicas.service.ContratacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contratacoes")
public class ContratacaoController {

	private final ContratacaoService contratacaoService;

	public ContratacaoController(ContratacaoService contratacaoService) {
		this.contratacaoService = contratacaoService;
	}

	@GetMapping
	public PaginaResposta<ContratacaoResponse> listar(
			@PageableDefault(size = 20, sort = "dataAssinatura", direction = Sort.Direction.DESC) Pageable paginacao) {
		return contratacaoService.listar(paginacao);
	}

	@GetMapping("/{id}")
	public ContratacaoResponse buscar(@PathVariable Long id) {
		return contratacaoService.buscar(id);
	}

	@PostMapping
	public ResponseEntity<ContratacaoResponse> criar(@Valid @RequestBody ContratacaoRequest dados) {
		ContratacaoResponse criada = contratacaoService.criar(dados);
		return ResponseEntity.created(URI.create("/api/contratacoes/" + criada.id())).body(criada);
	}

	@PutMapping("/{id}")
	public ContratacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ContratacaoRequest dados) {
		return contratacaoService.atualizar(id, dados);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Long id) {
		contratacaoService.excluir(id);
		return ResponseEntity.noContent().build();
	}
}
