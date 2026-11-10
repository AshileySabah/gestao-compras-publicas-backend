package br.fatec.compraspublicas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orgao")
public class Orgao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 20)
	private String codigo;

	@Column(nullable = false)
	private String nome;

	@Column(length = 20)
	private String codigoOrgaoSuperior;

	private String nomeOrgaoSuperior;

	protected Orgao() {
	}

	public Orgao(String codigo, String nome, String codigoOrgaoSuperior, String nomeOrgaoSuperior) {
		this.codigo = codigo;
		this.nome = nome;
		this.codigoOrgaoSuperior = codigoOrgaoSuperior;
		this.nomeOrgaoSuperior = nomeOrgaoSuperior;
	}

	public Long getId() {
		return id;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCodigoOrgaoSuperior() {
		return codigoOrgaoSuperior;
	}

	public void setCodigoOrgaoSuperior(String codigoOrgaoSuperior) {
		this.codigoOrgaoSuperior = codigoOrgaoSuperior;
	}

	public String getNomeOrgaoSuperior() {
		return nomeOrgaoSuperior;
	}

	public void setNomeOrgaoSuperior(String nomeOrgaoSuperior) {
		this.nomeOrgaoSuperior = nomeOrgaoSuperior;
	}
}
