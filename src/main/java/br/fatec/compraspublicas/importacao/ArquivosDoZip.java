package br.fatec.compraspublicas.importacao;

public record ArquivosDoZip(byte[] compras, byte[] itens) {

	public boolean possuiItens() {
		return itens != null;
	}
}
