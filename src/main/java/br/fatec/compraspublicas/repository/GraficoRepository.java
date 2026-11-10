package br.fatec.compraspublicas.repository;

import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import br.fatec.compraspublicas.dto.QuantidadePorModalidade;
import br.fatec.compraspublicas.dto.ValorPorOrgao;
import br.fatec.compraspublicas.model.Contratacao;

public interface GraficoRepository extends Repository<Contratacao, Long> {

	@Query("""
			select new br.fatec.compraspublicas.dto.ValorPorOrgao(o.codigo, o.nome, sum(c.valorFinal), min(c.nomeUg))
			from Contratacao c join c.orgao o
			group by o.codigo, o.nome
			order by sum(c.valorFinal) desc
			""")
	List<ValorPorOrgao> somarValorPorOrgao(Limit limite);

	@Query("""
			select new br.fatec.compraspublicas.dto.QuantidadePorModalidade(c.modalidade, count(c))
			from Contratacao c
			group by c.modalidade
			order by count(c) desc
			""")
	List<QuantidadePorModalidade> contarPorModalidade();

	@Query("""
			select new br.fatec.compraspublicas.repository.TotalDoMes(year(c.dataAssinatura), month(c.dataAssinatura), sum(c.valorFinal), count(c))
			from Contratacao c
			group by year(c.dataAssinatura), month(c.dataAssinatura)
			""")
	List<TotalDoMes> somarPorMes();
}
