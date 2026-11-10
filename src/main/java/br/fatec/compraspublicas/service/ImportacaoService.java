package br.fatec.compraspublicas.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.fatec.compraspublicas.dto.Contagem;
import br.fatec.compraspublicas.dto.LinhaRejeitada;
import br.fatec.compraspublicas.dto.ResultadoImportacao;
import br.fatec.compraspublicas.importacao.ArquivosDoZip;
import br.fatec.compraspublicas.importacao.ChaveContrato;
import br.fatec.compraspublicas.importacao.ColunasCsv;
import br.fatec.compraspublicas.importacao.FiltroDuplicados;
import br.fatec.compraspublicas.importacao.GravadorContratacoes;
import br.fatec.compraspublicas.importacao.LeitorCsv;
import br.fatec.compraspublicas.importacao.LeitorZip;
import br.fatec.compraspublicas.importacao.LinhaCompras;
import br.fatec.compraspublicas.importacao.LinhaCsv;
import br.fatec.compraspublicas.importacao.LinhaInvalidaException;
import br.fatec.compraspublicas.importacao.ModalidadePorItens;
import br.fatec.compraspublicas.importacao.ModalidadeResolvida;
import br.fatec.compraspublicas.importacao.ResolvedorModalidade;
import br.fatec.compraspublicas.importacao.ValidadorLinhaCompras;
import br.fatec.compraspublicas.model.Modalidade;
import br.fatec.compraspublicas.repository.ContratacaoRepository;

@Service
public class ImportacaoService {

	private final LeitorZip leitorZip;
	private final LeitorCsv leitorCsv;
	private final ValidadorLinhaCompras validador;
	private final ModalidadePorItens modalidadePorItens;
	private final ResolvedorModalidade resolvedorModalidade;
	private final FiltroDuplicados filtroDuplicados;
	private final GravadorContratacoes gravador;
	private final ContratacaoRepository contratacaoRepository;

	public ImportacaoService(LeitorZip leitorZip, LeitorCsv leitorCsv, ValidadorLinhaCompras validador,
			ModalidadePorItens modalidadePorItens, ResolvedorModalidade resolvedorModalidade,
			FiltroDuplicados filtroDuplicados, GravadorContratacoes gravador, ContratacaoRepository contratacaoRepository) {
		this.leitorZip = leitorZip;
		this.leitorCsv = leitorCsv;
		this.validador = validador;
		this.modalidadePorItens = modalidadePorItens;
		this.resolvedorModalidade = resolvedorModalidade;
		this.filtroDuplicados = filtroDuplicados;
		this.gravador = gravador;
		this.contratacaoRepository = contratacaoRepository;
	}

	public ResultadoImportacao importar(MultipartFile arquivo) {
		long inicio = System.nanoTime();

		ArquivosDoZip arquivos = leitorZip.ler(arquivo);
		List<LinhaCsv> linhasCompras = leitorCsv.ler(arquivos.compras(), "de contratos (Compras.csv)", ColunasCsv.OBRIGATORIAS_COMPRAS);
		List<LinhaCsv> linhasItens = arquivos.possuiItens()
				? leitorCsv.ler(arquivos.itens(), "de itens (ItemCompra.csv)", ColunasCsv.OBRIGATORIAS_ITENS)
				: List.of();

		List<LinhaCompras> validas = new ArrayList<>();
		List<LinhaRejeitada> rejeitadas = new ArrayList<>();
		for (LinhaCsv linha : linhasCompras) {
			try {
				validas.add(validador.validar(linha));
			} catch (LinhaInvalidaException excecao) {
				rejeitadas.add(LinhaRejeitada.de(linha.numero(), linha.valor(ColunasCsv.NUMERO_CONTRATO), excecao.getMotivo(), excecao.getMessage()));
			}
		}

		Set<ChaveContrato> chavesJaCadastradas = new HashSet<>(contratacaoRepository.buscarTodasAsChaves());
		FiltroDuplicados.Resultado semDuplicados = filtroDuplicados.filtrar(validas, chavesJaCadastradas);
		rejeitadas.addAll(semDuplicados.rejeitadas());
		rejeitadas.sort(Comparator.comparingLong(LinhaRejeitada::linha));

		Map<ChaveContrato, Modalidade> modalidadesDosItens = modalidadePorItens.mapear(linhasItens);
		Map<LinhaCompras, ModalidadeResolvida> modalidades = new LinkedHashMap<>();
		semDuplicados.mantidas().forEach(linha -> modalidades.put(linha, resolvedorModalidade.resolver(linha, modalidadesDosItens)));

		gravador.gravar(semDuplicados.mantidas(), modalidades);

		long tempoMs = (System.nanoTime() - inicio) / 1_000_000;
		return new ResultadoImportacao(
				linhasCompras.size(),
				semDuplicados.mantidas().size(),
				rejeitadas.size(),
				tempoMs,
				arquivos.possuiItens(),
				Contagem.agrupar(rejeitadas, LinhaRejeitada::motivo),
				Contagem.agrupar(new ArrayList<>(modalidades.values()), modalidade -> modalidade.origem().name()),
				rejeitadas);
	}
}
