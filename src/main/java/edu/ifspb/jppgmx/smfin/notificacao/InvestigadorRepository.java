package edu.ifspb.jppgmx.smfin.notificacao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;

@Repository
public class InvestigadorRepository {
    private final JdbcTemplate jdbcTemplate;

    public InvestigadorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Investigador save(Investigador investigador) {
        final String sql = "INSERT INTO Investigador(nome, funcao) VALUES (?, ?)";

        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, investigador.nome());
            ps.setString(2, investigador.funcao());
            return ps;
        }, keyHolder);

        if(keyHolder.getKeys() != null && keyHolder.getKeys().get("id") != null) {
            return new Investigador(
                (Integer) keyHolder.getKeys().get("id"),
                investigador.nome(),
                investigador.funcao()
            );
        } else {
            throw new IllegalStateException("O banco não retornou o ID gerado para o investigador.");
        }
    }

    public Investigador findById(Integer id) {
        final String sql = "SELECT * FROM Investigador WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            return new Investigador(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("funcao")
            );
        }, id);
    }
}
