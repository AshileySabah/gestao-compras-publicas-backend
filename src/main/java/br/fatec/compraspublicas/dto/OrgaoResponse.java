package br.fatec.compraspublicas.dto;

import br.fatec.compraspublicas.model.Orgao;

public record OrgaoResponse(Long id, String codigo, String nome, String codigoOrgaoSuperior, String nomeOrgaoSuperior) {

	public static OrgaoResponse de(Orgao orgao) {
		return new OrgaoResponse(orgao.getId(), orgao.getCodigo(), orgao.getNome(), orgao.getCodigoOrgaoSuperior(), orgao.getNomeOrgaoSuperior());
	}
}
