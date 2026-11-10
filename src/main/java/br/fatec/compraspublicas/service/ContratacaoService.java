package br.fatec.compraspublicas.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.fatec.compraspublicas.dto.ContratacaoRequest;
import br.fatec.compraspublicas.dto.ContratacaoResponse;
import br.fatec.compraspublicas.dto.FiltroContratacao;
import br.fatec.compraspublicas.dto.FornecedorRequest;
import br.fatec.compraspublicas.dto.OrgaoRequest;
import br.fatec.compraspublicas.dto.PaginaResposta;
import br.fatec.compraspublicas.exception.RecursoNaoEncontradoException;
import br.fatec.compraspublicas.exception.RegistroDuplicadoException;
import br.fatec.compraspublicas.exception.RegraNegocioException;
import br.fatec.compraspublicas.model.Contratacao;
import br.fatec.compraspublicas.model.Fornecedor;
import br.fatec.compraspublicas.model.Orgao;
import br.fatec.compraspublicas.model.OrigemRegistro;
import br.fatec.compraspublicas.model.TipoFornecedor;
import br.fatec.compraspublicas.repository.ContratacaoRepository;
import br.fatec.compraspublicas.repository.ContratacaoSpecifications;
import br.fatec.compraspublicas.repository.FornecedorRepository;
import br.fatec.compraspublicas.repository.OrgaoRepository;

@Service
public class ContratacaoService {

	private final ContratacaoRepository contratacaoRepository;
	private final OrgaoRepository orgaoRepository;
	private final FornecedorRepository fornecedorRepository;

	public ContratacaoService(ContratacaoRepository contratacaoRepository, OrgaoRepository orgaoRepository,
			FornecedorRepository fornecedorRepository) {
		this.contratacaoRepository = contratacaoRepository;
		this.orgaoRepository = orgaoRepository;
		this.fornecedorRepository = fornecedorRepository;
	}

	@Transactional(readOnly = true)
	public PaginaResposta<ContratacaoResponse> listar(FiltroContratacao filtro, Pageable paginacao) {
		validarIntervalos(filtro);
		return PaginaResposta.de(contratacaoRepository.findAll(ContratacaoSpecifications.comFiltros(filtro), paginacao)
				.map(ContratacaoResponse::de));
	}

	private void validarIntervalos(FiltroContratacao filtro) {
		if (filtro.dataInicio() != null && filtro.dataFim() != null && filtro.dataInicio().isAfter(filtro.dataFim())) {
			throw new RegraNegocioException("A data inicial do período não pode ser depois da data final.");
		}
		if (filtro.valorMinimo() != null && filtro.valorMaximo() != null && filtro.valorMinimo().compareTo(filtro.valorMaximo()) > 0) {
			throw new RegraNegocioException("O valor mínimo não pode ser maior que o valor máximo.");
		}
	}

	@Transactional(readOnly = true)
	public ContratacaoResponse buscar(Long id) {
		return ContratacaoResponse.de(encontrarContratacao(id));
	}

	@Transactional
	public ContratacaoResponse criar(ContratacaoRequest dados) {
		validarVigencia(dados);
		String numeroContrato = limpar(dados.numeroContrato());
		String codigoUg = limpar(dados.codigoUg());
		if (contratacaoRepository.existsByNumeroContratoAndCodigoUg(numeroContrato, codigoUg)) {
			throw contratoJaCadastrado(numeroContrato, codigoUg);
		}
		Contratacao contratacao = new Contratacao(numeroContrato, codigoUg, resolverOrgao(dados), resolverFornecedor(dados),
				dados.dataAssinatura(), dados.valorFinal(), OrigemRegistro.CADASTRO_MANUAL);
		preencherDadosEditaveis(contratacao, dados);
		return ContratacaoResponse.de(contratacaoRepository.save(contratacao));
	}

	@Transactional
	public ContratacaoResponse atualizar(Long id, ContratacaoRequest dados) {
		Contratacao contratacao = encontrarContratacao(id);
		validarVigencia(dados);
		String numeroContrato = limpar(dados.numeroContrato());
		String codigoUg = limpar(dados.codigoUg());
		if (contratacaoRepository.existsByNumeroContratoAndCodigoUgAndIdNot(numeroContrato, codigoUg, id)) {
			throw contratoJaCadastrado(numeroContrato, codigoUg);
		}
		contratacao.setNumeroContrato(numeroContrato);
		contratacao.setCodigoUg(codigoUg);
		contratacao.setOrgao(resolverOrgao(dados));
		contratacao.setFornecedor(resolverFornecedor(dados));
		contratacao.setDataAssinatura(dados.dataAssinatura());
		contratacao.setValorFinal(dados.valorFinal());
		preencherDadosEditaveis(contratacao, dados);
		return ContratacaoResponse.de(contratacao);
	}

	@Transactional
	public void excluir(Long id) {
		contratacaoRepository.delete(encontrarContratacao(id));
	}

	private Contratacao encontrarContratacao(Long id) {
		return contratacaoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Contratação " + id + " não encontrada."));
	}

	private void preencherDadosEditaveis(Contratacao contratacao, ContratacaoRequest dados) {
		contratacao.setNomeUg(limpar(dados.nomeUg()));
		contratacao.setObjeto(limpar(dados.objeto()));
		contratacao.setDataPublicacaoDou(dados.dataPublicacaoDou());
		contratacao.setDataInicioVigencia(dados.dataInicioVigencia());
		contratacao.setDataFimVigencia(dados.dataFimVigencia());
		contratacao.setValorInicial(dados.valorInicial());
		contratacao.setNumeroLicitacao(limpar(dados.numeroLicitacao()));
		contratacao.informarModalidadeManualmente(dados.modalidade());
	}

	private void validarVigencia(ContratacaoRequest dados) {
		if (dados.dataInicioVigencia() != null && dados.dataFimVigencia() != null
				&& dados.dataFimVigencia().isBefore(dados.dataInicioVigencia())) {
			throw new RegraNegocioException("A data de fim da vigência não pode ser anterior à data de início.");
		}
	}

	private Orgao resolverOrgao(ContratacaoRequest dados) {
		if ((dados.orgaoId() == null) == (dados.novoOrgao() == null)) {
			throw new RegraNegocioException("Escolha um órgão existente ou informe os dados de um novo órgão (apenas uma das opções).");
		}
		if (dados.orgaoId() != null) {
			return orgaoRepository.findById(dados.orgaoId())
					.orElseThrow(() -> new RecursoNaoEncontradoException("Órgão " + dados.orgaoId() + " não encontrado."));
		}
		OrgaoRequest novoOrgao = dados.novoOrgao();
		String codigo = limpar(novoOrgao.codigo());
		if (orgaoRepository.findByCodigo(codigo).isPresent()) {
			throw new RegistroDuplicadoException("Já existe um órgão com o código " + codigo + ". Escolha-o na lista de órgãos.");
		}
		return orgaoRepository.save(new Orgao(codigo, limpar(novoOrgao.nome()),
				limpar(novoOrgao.codigoOrgaoSuperior()), limpar(novoOrgao.nomeOrgaoSuperior())));
	}

	private Fornecedor resolverFornecedor(ContratacaoRequest dados) {
		if ((dados.fornecedorId() == null) == (dados.novoFornecedor() == null)) {
			throw new RegraNegocioException("Escolha um fornecedor existente ou informe os dados de um novo fornecedor (apenas uma das opções).");
		}
		if (dados.fornecedorId() != null) {
			return fornecedorRepository.findById(dados.fornecedorId())
					.orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor " + dados.fornecedorId() + " não encontrado."));
		}
		FornecedorRequest novoFornecedor = dados.novoFornecedor();
		String documento = limpar(novoFornecedor.documento()).toUpperCase();
		TipoFornecedor tipo = TipoFornecedor.deDocumento(documento)
				.orElseThrow(() -> new RegraNegocioException("Documento do fornecedor inválido. Informe CPF (11 dígitos), CNPJ (14 dígitos), código de estrangeiro iniciado por EX ou -11 para sigiloso."));
		if (fornecedorRepository.findByDocumento(documento).isPresent()) {
			throw new RegistroDuplicadoException("Já existe um fornecedor com o documento " + documento + ". Escolha-o na lista de fornecedores.");
		}
		return fornecedorRepository.save(new Fornecedor(documento, limpar(novoFornecedor.nome()), tipo));
	}

	private RegistroDuplicadoException contratoJaCadastrado(String numeroContrato, String codigoUg) {
		return new RegistroDuplicadoException("Já existe o contrato " + numeroContrato + " na UG " + codigoUg + ".");
	}

	private String limpar(String texto) {
		return texto == null || texto.isBlank() ? null : texto.trim();
	}
}
