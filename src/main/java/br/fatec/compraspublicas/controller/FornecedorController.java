package br.fatec.compraspublicas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.fatec.compraspublicas.dto.FornecedorResponse;
import br.fatec.compraspublicas.service.FornecedorService;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

	private final FornecedorService fornecedorService;

	public FornecedorController(FornecedorService fornecedorService) {
		this.fornecedorService = fornecedorService;
	}

	@GetMapping
	public List<FornecedorResponse> buscar(@RequestParam(required = false) String busca) {
		return fornecedorService.buscar(busca);
	}
}
