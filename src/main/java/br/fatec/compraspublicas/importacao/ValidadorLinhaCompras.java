package br.fatec.compraspublicas.importacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import br.fatec.compraspublicas.model.TipoFornecedor;

@Component
public class ValidadorLinhaCompras {

	private static final List<String> CAMPOS_OBRIGATORIOS = List.of(ColunasCsv.NUMERO_CONTRATO, ColunasCsv.CODIGO_UG,
			ColunasCsv.CODIGO_ORGAO, ColunasCsv.NOME_ORGAO, ColunasCsv.CODIGO_CONTRATADO, ColunasCsv.NOME_CONTRATADO,
			ColunasCsv.DATA_ASSINATURA, ColunasCsv.VALOR_FINAL);
	private static final String PREFIXO_OBJETO = "Objeto:";
	private static final String LICITACAO_INEXISTENTE = "-3";

	private final ConversorDados conversor;

	public ValidadorLinhaCompras(ConversorDados conversor) {
		this.conversor = conversor;
	}

	public LinhaCompras validar(LinhaCsv linha) {
		if (!linha.completa()) {
			throw new LinhaInvalidaException(MotivoRejeicao.COLUNAS_INCOMPLETAS, "A linha não tem as mesmas colunas do cabeçalho");
		}
		List<String> camposVazios = CAMPOS_OBRIGATORIOS.stream().filter(coluna -> linha.valor(coluna).isEmpty()).toList();
		if (!camposVazios.isEmpty()) {
			throw new LinhaInvalidaException(MotivoRejeicao.CAMPO_OBRIGATORIO_VAZIO, String.join(", ", camposVazios));
		}

		String documento = linha.valor(ColunasCsv.CODIGO_CONTRATADO);
		TipoFornecedor tipoFornecedor = TipoFornecedor.deDocumento(documento)
				.orElseThrow(() -> new LinhaInvalidaException(MotivoRejeicao.DOCUMENTO_INVALIDO, "Código Contratado \"" + documento + "\" não é CPF, CNPJ, estrangeiro nem sigiloso"));

		BigDecimal valorInicial = conversor.valor(linha.valor(ColunasCsv.VALOR_INICIAL), ColunasCsv.VALOR_INICIAL);
		BigDecimal valorFinal = conversor.valor(linha.valor(ColunasCsv.VALOR_FINAL), ColunasCsv.VALOR_FINAL);

		LocalDate dataAssinatura = conversor.data(linha.valor(ColunasCsv.DATA_ASSINATURA), ColunasCsv.DATA_ASSINATURA);
		LocalDate dataPublicacao = conversor.data(linha.valor(ColunasCsv.DATA_PUBLICACAO_DOU), ColunasCsv.DATA_PUBLICACAO_DOU);
		LocalDate inicioVigencia = conversor.data(linha.valor(ColunasCsv.DATA_INICIO_VIGENCIA), ColunasCsv.DATA_INICIO_VIGENCIA);
		LocalDate fimVigencia = conversor.data(linha.valor(ColunasCsv.DATA_FIM_VIGENCIA), ColunasCsv.DATA_FIM_VIGENCIA);
		if (inicioVigencia != null && fimVigencia != null && fimVigencia.isBefore(inicioVigencia)) {
			throw new LinhaInvalidaException(MotivoRejeicao.VIGENCIA_INVALIDA, "Início " + linha.valor(ColunasCsv.DATA_INICIO_VIGENCIA) + ", fim " + linha.valor(ColunasCsv.DATA_FIM_VIGENCIA));
		}

		return new LinhaCompras(
				linha,
				linha.valor(ColunasCsv.NUMERO_CONTRATO),
				linha.valor(ColunasCsv.CODIGO_UG),
				conversor.texto(linha.valor(ColunasCsv.NOME_UG)),
				limparObjeto(linha.valor(ColunasCsv.OBJETO)),
				linha.valor(ColunasCsv.MODALIDADE_COMPRA),
				linha.valor(ColunasCsv.MODALIDADE_COMPRA_LICITACAO),
				conversor.texto(linha.valor(ColunasCsv.CODIGO_ORGAO_SUPERIOR)),
				conversor.texto(linha.valor(ColunasCsv.NOME_ORGAO_SUPERIOR)),
				linha.valor(ColunasCsv.CODIGO_ORGAO),
				linha.valor(ColunasCsv.NOME_ORGAO),
				dataAssinatura,
				dataPublicacao,
				inicioVigencia,
				fimVigencia,
				documento,
				linha.valor(ColunasCsv.NOME_CONTRATADO),
				tipoFornecedor,
				valorInicial,
				valorFinal,
				limparNumeroLicitacao(linha.valor(ColunasCsv.NUMERO_LICITACAO)));
	}

	private String limparObjeto(String objeto) {
		String semPrefixo = objeto.startsWith(PREFIXO_OBJETO) ? objeto.substring(PREFIXO_OBJETO.length()) : objeto;
		return conversor.texto(semPrefixo);
	}

	private String limparNumeroLicitacao(String numeroLicitacao) {
		return LICITACAO_INEXISTENTE.equals(numeroLicitacao) ? null : conversor.texto(numeroLicitacao);
	}
}
