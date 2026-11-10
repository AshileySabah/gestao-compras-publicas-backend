package br.fatec.compraspublicas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.fatec.compraspublicas.dto.QuantidadePorModalidade;
import br.fatec.compraspublicas.dto.ValorPorMes;
import br.fatec.compraspublicas.dto.ValorPorOrgao;
import br.fatec.compraspublicas.service.GraficoService;

@RestController
@RequestMapping("/api/graficos")
public class GraficoController {

	private final GraficoService graficoService;

	public GraficoController(GraficoService graficoService) {
		this.graficoService = graficoService;
	}

	@GetMapping("/valor-por-orgao")
	public List<ValorPorOrgao> valorPorOrgao() {
		return graficoService.valorPorOrgao();
	}

	@GetMapping("/quantidade-por-modalidade")
	public List<QuantidadePorModalidade> quantidadePorModalidade() {
		return graficoService.quantidadePorModalidade();
	}

	@GetMapping("/evolucao-mensal")
	public List<ValorPorMes> evolucaoMensal() {
		return graficoService.evolucaoMensal();
	}
}
