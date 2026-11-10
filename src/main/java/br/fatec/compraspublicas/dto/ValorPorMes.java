package br.fatec.compraspublicas.dto;

import java.math.BigDecimal;

public record ValorPorMes(String mes, BigDecimal valorTotal, long quantidade) {
}
