package br.fatec.compraspublicas.repository;

import java.math.BigDecimal;

public record TotalDoMes(Integer ano, Integer mes, BigDecimal valorTotal, Long quantidade) {
}
