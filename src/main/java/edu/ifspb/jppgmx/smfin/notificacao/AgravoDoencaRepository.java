package edu.ifspb.jppgmx.smfin.notificacao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AgravoDoencaRepository {
    private final JdbcTemplate jdbcTemplate;

    public AgravoDoencaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AgravoDoenca findByCid10(String cid10) {
        String sql = "SELECT cid10, nome FROM AgravoDoenca WHERE cid10 = ?";

        var row = jdbcTemplate.queryForList(sql, cid10).stream().findFirst().orElse(null);
        if (row == null) {
            return null;
        }

        return new AgravoDoenca(
                (String) row.get("cid10"),
                (String) row.get("nome")
        );
    }

    public List<AgravoDoenca> findAll() {
        String sql = "SELECT cid10, nome FROM AgravoDoenca";

        var rows = jdbcTemplate.queryForList(sql);
        return rows.stream()
                .map(row -> new AgravoDoenca(
                        (String) row.get("cid10"),
                        (String) row.get("nome")
                ))
                .toList();
    }
}
