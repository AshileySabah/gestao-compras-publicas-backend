package br.fatec.compraspublicas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.fatec.compraspublicas.dto.OrgaoResponse;
import br.fatec.compraspublicas.service.OrgaoService;

@RestController
@RequestMapping("/api/orgaos")
public class OrgaoController {

	private final OrgaoService orgaoService;

	public OrgaoController(OrgaoService orgaoService) {
		this.orgaoService = orgaoService;
	}

	@GetMapping
	public List<OrgaoResponse> listar() {
		return orgaoService.listar();
	}
}
