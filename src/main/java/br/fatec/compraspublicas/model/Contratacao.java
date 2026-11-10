package br.fatec.compraspublicas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "contratacao", uniqueConstraints = @UniqueConstraint(name = "uk_contratacao_numero_ug", columnNames = { "numero_contrato", "codigo_ug" }))
public class Contratacao {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contratacao_seq")
	@SequenceGenerator(name = "contratacao_seq", sequenceName = "contratacao_seq", allocationSize = 50)
	private Long id;

	@Column(name = "numero_contrato", nullable = false, length = 30)
	private String numeroContrato;

	@Column(name = "codigo_ug", nullable = false, length = 20)
	private String codigoUg;

	private String nomeUg;

	@Column(columnDefinition = "text")
	private String objeto;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 60)
	private Modalidade modalidade;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private OrigemModalidade origemModalidade;

	@Column(nullable = false)
	private LocalDate dataAssinatura;

	private LocalDate dataPublicacaoDou;

	private LocalDate dataInicioVigencia;

	private LocalDate dataFimVigencia;

	@Column(precision = 19, scale = 4)
	private BigDecimal valorInicial;

	@Column(nullable = false, precision = 19, scale = 4)
	private BigDecimal valorFinal;

	@Column(length = 30)
	private String numeroLicitacao;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrigemRegistro origemRegistro;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "orgao_id", nullable = false)
	private Orgao orgao;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "fornecedor_id", nullable = false)
	private Fornecedor fornecedor;

	protected Contratacao() {
	}

	public Contratacao(String numeroContrato, String codigoUg, Orgao orgao, Fornecedor fornecedor,
			LocalDate dataAssinatura, BigDecimal valorFinal, OrigemRegistro origemRegistro) {
		this.numeroContrato = numeroContrato;
		this.codigoUg = codigoUg;
		this.orgao = orgao;
		this.fornecedor = fornecedor;
		this.dataAssinatura = dataAssinatura;
		this.valorFinal = valorFinal;
		this.origemRegistro = origemRegistro;
		this.modalidade = Modalidade.NAO_INFORMADA;
		this.origemModalidade = OrigemModalidade.NAO_INFORMADA;
	}

	public void definirModalidade(Modalidade modalidade, OrigemModalidade origemModalidade) {
		this.modalidade = modalidade;
		this.origemModalidade = origemModalidade;
	}

	public void informarModalidadeManualmente(Modalidade modalidadeInformada) {
		Modalidade novaModalidade = modalidadeInformada == null ? Modalidade.NAO_INFORMADA : modalidadeInformada;
		if (novaModalidade == this.modalidade) {
			return;
		}
		OrigemModalidade novaOrigem = novaModalidade == Modalidade.NAO_INFORMADA ? OrigemModalidade.NAO_INFORMADA : OrigemModalidade.INFORMADA_MANUALMENTE;
		definirModalidade(novaModalidade, novaOrigem);
	}

	public Long getId() {
		return id;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public String getCodigoUg() {
		return codigoUg;
	}

	public void setCodigoUg(String codigoUg) {
		this.codigoUg = codigoUg;
	}

	public String getNomeUg() {
		return nomeUg;
	}

	public void setNomeUg(String nomeUg) {
		this.nomeUg = nomeUg;
	}

	public String getObjeto() {
		return objeto;
	}

	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}

	public Modalidade getModalidade() {
		return modalidade;
	}

	public OrigemModalidade getOrigemModalidade() {
		return origemModalidade;
	}

	public LocalDate getDataAssinatura() {
		return dataAssinatura;
	}

	public void setDataAssinatura(LocalDate dataAssinatura) {
		this.dataAssinatura = dataAssinatura;
	}

	public LocalDate getDataPublicacaoDou() {
		return dataPublicacaoDou;
	}

	public void setDataPublicacaoDou(LocalDate dataPublicacaoDou) {
		this.dataPublicacaoDou = dataPublicacaoDou;
	}

	public LocalDate getDataInicioVigencia() {
		return dataInicioVigencia;
	}

	public void setDataInicioVigencia(LocalDate dataInicioVigencia) {
		this.dataInicioVigencia = dataInicioVigencia;
	}

	public LocalDate getDataFimVigencia() {
		return dataFimVigencia;
	}

	public void setDataFimVigencia(LocalDate dataFimVigencia) {
		this.dataFimVigencia = dataFimVigencia;
	}

	public BigDecimal getValorInicial() {
		return valorInicial;
	}

	public void setValorInicial(BigDecimal valorInicial) {
		this.valorInicial = valorInicial;
	}

	public BigDecimal getValorFinal() {
		return valorFinal;
	}

	public void setValorFinal(BigDecimal valorFinal) {
		this.valorFinal = valorFinal;
	}

	public String getNumeroLicitacao() {
		return numeroLicitacao;
	}

	public void setNumeroLicitacao(String numeroLicitacao) {
		this.numeroLicitacao = numeroLicitacao;
	}

	public OrigemRegistro getOrigemRegistro() {
		return origemRegistro;
	}

	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
}
