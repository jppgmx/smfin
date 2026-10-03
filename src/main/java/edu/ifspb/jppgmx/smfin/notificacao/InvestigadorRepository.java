package edu.ifspb.jppgmx.smfin.notificacao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.util.List;

@Repository
public class InvestigadorRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UnidadeRepository unidadeRepository;

    public InvestigadorRepository(JdbcTemplate jdbcTemplate, UnidadeRepository unidadeRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.unidadeRepository = unidadeRepository;
    }

    public Investigador save(Investigador investigador) {
        final String sql = "INSERT INTO Investigador(nome, funcao, unidade_codigo) VALUES (?, ?, ?)";

        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, investigador.nome());
            ps.setString(2, investigador.funcao());
            ps.setString(3, investigador.unidade().codigo());
            return ps;
        }, keyHolder);

        if(keyHolder.getKeys() != null && keyHolder.getKeys().get("id") != null) {
            return new Investigador(
                (Integer) keyHolder.getKeys().get("id"),
                investigador.nome(),
                investigador.funcao(),
                investigador.unidade()
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
                rs.getString("funcao"),
                unidadeRepository.findByCodigo(rs.getString("unidade_codigo"))
            );
        }, id);
    }

    public List<Investigador> findAll() {
        final String sql = "SELECT * FROM Investigador";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Investigador(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("funcao"),
                unidadeRepository.findByCodigo(rs.getString("unidade_codigo"))
            );
        });
    }

    public Investigador fromUnidade(Unidade unidade, Integer id) {
        final String sql = "SELECT * FROM Investigador WHERE id = ? AND unidade_codigo = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            return new Investigador(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("funcao"),
                unidade
            );
        }, id, unidade.codigo());
    }
}
