package br.fatec.compraspublicas;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class ConexaoBancoTest {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void conectaNoPostgresDoNeon() {
		String versao = jdbcTemplate.queryForObject("select version()", String.class);

		assertThat(versao).startsWith("PostgreSQL");
	}
}
