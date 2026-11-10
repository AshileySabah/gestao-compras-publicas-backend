package br.fatec.compraspublicas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.fatec.compraspublicas.dto.ModalidadeResponse;

@RestController
@RequestMapping("/api/modalidades")
public class ModalidadeController {

	@GetMapping
	public List<ModalidadeResponse> listar() {
		return ModalidadeResponse.todas();
	}
}
