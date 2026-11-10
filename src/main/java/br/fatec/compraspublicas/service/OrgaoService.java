package br.fatec.compraspublicas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.fatec.compraspublicas.dto.OrgaoResponse;
import br.fatec.compraspublicas.repository.OrgaoRepository;

@Service
public class OrgaoService {

	private final OrgaoRepository orgaoRepository;

	public OrgaoService(OrgaoRepository orgaoRepository) {
		this.orgaoRepository = orgaoRepository;
	}

	@Transactional(readOnly = true)
	public List<OrgaoResponse> listar() {
		return orgaoRepository.findAllByOrderByNomeAscCodigoAsc().stream().map(OrgaoResponse::de).toList();
	}
}
