package br.fatec.compraspublicas.dto;

import java.math.BigDecimal;

public record ValorPorOrgao(String codigo, String nome, BigDecimal valorTotal, String unidadeExemplo) {
}
