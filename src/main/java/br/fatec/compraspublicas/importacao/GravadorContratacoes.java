package br.fatec.compraspublicas.importacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.fatec.compraspublicas.model.Contratacao;
import br.fatec.compraspublicas.model.Fornecedor;
import br.fatec.compraspublicas.model.Orgao;
import br.fatec.compraspublicas.model.OrigemRegistro;
import br.fatec.compraspublicas.repository.ContratacaoRepository;
import br.fatec.compraspublicas.repository.FornecedorRepository;
import br.fatec.compraspublicas.repository.OrgaoRepository;

@Component
public class GravadorContratacoes {

	private final ContratacaoRepository contratacaoRepository;
	private final OrgaoRepository orgaoRepository;
	private final FornecedorRepository fornecedorRepository;

	public GravadorContratacoes(ContratacaoRepository contratacaoRepository, OrgaoRepository orgaoRepository,
			FornecedorRepository fornecedorRepository) {
		this.contratacaoRepository = contratacaoRepository;
		this.orgaoRepository = orgaoRepository;
		this.fornecedorRepository = fornecedorRepository;
	}

	@Transactional
	public void gravar(List<LinhaCompras> linhas, Map<LinhaCompras, ModalidadeResolvida> modalidades) {
		Map<String, Orgao> orgaosPorCodigo = orgaoRepository.findAll().stream()
				.collect(Collectors.toMap(Orgao::getCodigo, Function.identity()));
		Map<String, Fornecedor> fornecedoresPorDocumento = fornecedorRepository.findAll().stream()
				.collect(Collectors.toMap(Fornecedor::getDocumento, Function.identity()));

		List<Contratacao> contratacoes = new ArrayList<>();
		for (LinhaCompras linha : linhas) {
			Orgao orgao = orgaosPorCodigo.computeIfAbsent(linha.codigoOrgao(), codigo -> orgaoRepository.save(
					new Orgao(codigo, linha.nomeOrgao(), linha.codigoOrgaoSuperior(), linha.nomeOrgaoSuperior())));
			Fornecedor fornecedor = fornecedoresPorDocumento.computeIfAbsent(linha.documentoContratado(), documento -> fornecedorRepository.save(
					new Fornecedor(documento, linha.nomeContratado(), linha.tipoFornecedor())));
			contratacoes.add(montar(linha, orgao, fornecedor, modalidades.get(linha)));
		}
		contratacaoRepository.saveAll(contratacoes);
	}

	private Contratacao montar(LinhaCompras linha, Orgao orgao, Fornecedor fornecedor, ModalidadeResolvida modalidade) {
		Contratacao contratacao = new Contratacao(linha.numeroContrato(), linha.codigoUg(), orgao, fornecedor,
				linha.dataAssinatura(), linha.valorFinal(), OrigemRegistro.IMPORTACAO);
		contratacao.setNomeUg(linha.nomeUg());
		contratacao.setObjeto(linha.objeto());
		contratacao.setDataPublicacaoDou(linha.dataPublicacaoDou());
		contratacao.setDataInicioVigencia(linha.dataInicioVigencia());
		contratacao.setDataFimVigencia(linha.dataFimVigencia());
		contratacao.setValorInicial(linha.valorInicial());
		contratacao.setNumeroLicitacao(linha.numeroLicitacao());
		contratacao.definirModalidade(modalidade.modalidade(), modalidade.origem());
		return contratacao;
	}
}
