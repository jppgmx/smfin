package edu.ifspb.jppgmx.smfin.notificacao;

import edu.ifspb.jppgmx.smfin.DominioCodificado;
import edu.ifspb.jppgmx.smfin.localidade.LocalidadeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UnidadeRepository {
    private final JdbcTemplate jdbcTemplate;
    private final LocalidadeRepository localidadeRepository;

    public UnidadeRepository(JdbcTemplate jdbcTemplate, LocalidadeRepository localidadeRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.localidadeRepository = localidadeRepository;
    }

    public Unidade save(Unidade unidade) {
        final String sql = "INSERT INTO Unidade(codigo, nome, tipo, municipio_codigo_ibge) VALUES (?, ?, ?, ?)";

        var municipio = localidadeRepository.save(unidade.municipio());
        jdbcTemplate.update(sql, unidade.codigo(), unidade.nome(), unidade.tipo().getCodigo(), municipio.codigoIbge());

        return new Unidade(unidade.codigo(), unidade.nome(), unidade.tipo(), municipio);
    }

    public Unidade findByCodigo(String codigo) {
        final String sql = "SELECT * FROM Unidade WHERE codigo = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            var municipio = localidadeRepository.findMunicipioByCodigoIbge(rs.getInt("municipio_codigo_ibge"));
            return new Unidade(
                rs.getString("codigo"),
                rs.getString("nome"),
                DominioCodificado.fromCodigo(rs.getInt("tipo"), Unidade.Tipo.class),
                municipio
            );
        }, codigo);
    }

    public List<Unidade> findAll() {
        final String sql = "SELECT * FROM Unidade";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            var municipio = localidadeRepository.findMunicipioByCodigoIbge(rs.getInt("municipio_codigo_ibge"));
            return new Unidade(
                rs.getString("codigo"),
                rs.getString("nome"),
                DominioCodificado.fromCodigo(rs.getInt("tipo"), Unidade.Tipo.class),
                municipio
            );
        });
    }

    public List<Unidade> findByMunicipio(Integer municipioCodigoIbge) {
        final String sql = "SELECT * FROM Unidade WHERE municipio_codigo_ibge = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Unidade(
                rs.getString("codigo"),
                rs.getString("nome"),
                DominioCodificado.fromCodigo(rs.getInt("tipo"), Unidade.Tipo.class),
                localidadeRepository.findMunicipioByCodigoIbge(rs.getInt("municipio_codigo_ibge"))
        ), municipioCodigoIbge);
    }

    public List<Investigador> findInvestigadoresByUnidade(Unidade unidade) {
        final String sql = "SELECT i.* FROM Investigador i WHERE i.unidade_codigo = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Investigador(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("funcao"),
            unidade
        ), unidade.codigo());
    }
}
