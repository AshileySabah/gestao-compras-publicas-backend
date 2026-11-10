package br.fatec.compraspublicas.importacao;

public class LinhaInvalidaException extends RuntimeException {

	private final MotivoRejeicao motivo;

	public LinhaInvalidaException(MotivoRejeicao motivo, String detalhe) {
		super(detalhe);
		this.motivo = motivo;
	}

	public MotivoRejeicao getMotivo() {
		return motivo;
	}
}
