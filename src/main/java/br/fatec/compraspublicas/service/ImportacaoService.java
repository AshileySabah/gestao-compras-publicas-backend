package br.fatec.compraspublicas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.fatec.compraspublicas.dto.ResultadoImportacao;
import br.fatec.compraspublicas.importacao.ArquivosDoZip;
import br.fatec.compraspublicas.importacao.ColunasCsv;
import br.fatec.compraspublicas.importacao.LeitorCsv;
import br.fatec.compraspublicas.importacao.LeitorZip;
import br.fatec.compraspublicas.importacao.LinhaCsv;

@Service
public class ImportacaoService {

	private final LeitorZip leitorZip;
	private final LeitorCsv leitorCsv;

	public ImportacaoService(LeitorZip leitorZip, LeitorCsv leitorCsv) {
		this.leitorZip = leitorZip;
		this.leitorCsv = leitorCsv;
	}

	public ResultadoImportacao importar(MultipartFile arquivo) {
		ArquivosDoZip arquivos = leitorZip.ler(arquivo);
		List<LinhaCsv> linhasCompras = leitorCsv.ler(arquivos.compras(), "de contratos (Compras.csv)", ColunasCsv.OBRIGATORIAS_COMPRAS);
		List<LinhaCsv> linhasItens = arquivos.possuiItens()
				? leitorCsv.ler(arquivos.itens(), "de itens (ItemCompra.csv)", ColunasCsv.OBRIGATORIAS_ITENS)
				: List.of();
		return new ResultadoImportacao(linhasCompras.size(), linhasItens.size());
	}
}
