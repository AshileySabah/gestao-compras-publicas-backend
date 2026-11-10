package br.fatec.compraspublicas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "fornecedor")
public class Fornecedor {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fornecedor_seq")
	@SequenceGenerator(name = "fornecedor_seq", sequenceName = "fornecedor_seq", allocationSize = 50)
	private Long id;

	@Column(nullable = false, unique = true, length = 20)
	private String documento;

	@Column(nullable = false)
	private String nome;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoFornecedor tipo;

	protected Fornecedor() {
	}

	public Fornecedor(String documento, String nome, TipoFornecedor tipo) {
		this.documento = documento;
		this.nome = nome;
		this.tipo = tipo;
	}

	public Long getId() {
		return id;
	}

	public String getDocumento() {
		return documento;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public TipoFornecedor getTipo() {
		return tipo;
	}
}
