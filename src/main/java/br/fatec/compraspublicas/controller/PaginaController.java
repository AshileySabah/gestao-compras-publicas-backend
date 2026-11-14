package br.fatec.compraspublicas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {

	@GetMapping({ "/", "/contratacoes", "/contratacoes/{id}", "/importacao", "/dashboard" })
	public String abrirAplicacao() {
		return "forward:/index.html";
	}
}
