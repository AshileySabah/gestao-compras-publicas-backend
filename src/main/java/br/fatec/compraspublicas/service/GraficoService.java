package br.fatec.compraspublicas.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.fatec.compraspublicas.dto.QuantidadePorModalidade;
import br.fatec.compraspublicas.dto.ValorPorMes;
import br.fatec.compraspublicas.dto.ValorPorOrgao;
import br.fatec.compraspublicas.repository.GraficoRepository;
import br.fatec.compraspublicas.repository.TotalDoMes;

@Service
public class GraficoService {

	private static final int QUANTIDADE_DE_ORGAOS = 10;
	private static final int QUANTIDADE_DE_MESES = 12;

	private final GraficoRepository graficoRepository;

	public GraficoService(GraficoRepository graficoRepository) {
		this.graficoRepository = graficoRepository;
	}

	@Transactional(readOnly = true)
	public List<ValorPorOrgao> valorPorOrgao() {
		return graficoRepository.somarValorPorOrgao(Limit.of(QUANTIDADE_DE_ORGAOS));
	}

	@Transactional(readOnly = true)
	public List<QuantidadePorModalidade> quantidadePorModalidade() {
		return graficoRepository.contarPorModalidade();
	}

	@Transactional(readOnly = true)
	public List<ValorPorMes> evolucaoMensal() {
		Map<YearMonth, TotalDoMes> totaisPorMes = graficoRepository.somarPorMes().stream()
				.collect(Collectors.toMap(total -> YearMonth.of(total.ano(), total.mes()), Function.identity()));
		if (totaisPorMes.isEmpty()) {
			return List.of();
		}
		YearMonth mesMaisRecente = totaisPorMes.keySet().stream().max(Comparator.naturalOrder()).orElseThrow();
		List<ValorPorMes> meses = new ArrayList<>();
		for (YearMonth mes = mesMaisRecente.minusMonths(QUANTIDADE_DE_MESES - 1); !mes.isAfter(mesMaisRecente); mes = mes.plusMonths(1)) {
			TotalDoMes total = totaisPorMes.get(mes);
			meses.add(total == null
					? new ValorPorMes(mes.toString(), BigDecimal.ZERO, 0)
					: new ValorPorMes(mes.toString(), total.valorTotal(), total.quantidade()));
		}
		return meses;
	}
}
