package br.fatec.compraspublicas.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.fatec.compraspublicas.dto.ResultadoImportacao;
import br.fatec.compraspublicas.service.ImportacaoService;

@RestController
@RequestMapping("/api/importacoes")
public class ImportacaoController {

	private final ImportacaoService importacaoService;

	public ImportacaoController(ImportacaoService importacaoService) {
		this.importacaoService = importacaoService;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResultadoImportacao importar(@RequestParam("arquivo") MultipartFile arquivo) {
		return importacaoService.importar(arquivo);
	}
}
