package edu.ifspb.jppgmx.smfin.localidade;

import edu.ifspb.jppgmx.smfin.DominioCodificado;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;

@Repository
public class LocalidadeRepository {
    private final JdbcTemplate jdbcTemplate;

    public LocalidadeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public EnderecoResidencial save(EnderecoResidencial enderecoResidencial) {
        final String sql = "INSERT INTO EnderecoResidencial(id_localidade, logradouro, codigo_logradouro, numero, complemento, geocampo1, geocampo2, pontoReferencia, cep, zona, telefone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        var localidade = save(enderecoResidencial.base());
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, localidade.id());
            ps.setString(2, enderecoResidencial.logradouro());
            ps.setString(3, enderecoResidencial.codigoLogradouro());
            ps.setString(4, enderecoResidencial.numero());
            ps.setString(5, enderecoResidencial.complemento());
            ps.setString(6, enderecoResidencial.geocampo1());
            ps.setString(7, enderecoResidencial.geocampo2());
            ps.setString(8, enderecoResidencial.pontoReferencia());
            ps.setString(9, enderecoResidencial.cep());
            ps.setInt(10, enderecoResidencial.zona().getCodigo());
            ps.setString(11, enderecoResidencial.telefone());
            return ps;
        }, keyHolder);

        if(keyHolder.getKeys() != null && keyHolder.getKeys().get("id") != null) {
            return new EnderecoResidencial(
                (Integer) keyHolder.getKeys().get("id"),
                localidade,
                enderecoResidencial.logradouro(),
                enderecoResidencial.codigoLogradouro(),
                enderecoResidencial.numero(),
                enderecoResidencial.complemento(),
                enderecoResidencial.geocampo1(),
                enderecoResidencial.geocampo2(),
                enderecoResidencial.pontoReferencia(),
                enderecoResidencial.cep(),
                enderecoResidencial.telefone(),
                enderecoResidencial.zona()
            );
        } else {
            throw new IllegalStateException("O banco não retornou o ID gerado para o endereço residencial.");
        }
    }

    public Localidade save(Localidade localidade) {
        final String sql = "INSERT INTO Localidade(pais, distrito, bairro, cidade_codigo_ibge) VALUES (?, ?, ?, ?)";

        var municipio = save(localidade.municipio());
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, localidade.pais());
            ps.setString(2, localidade.distrito());
            ps.setString(3, localidade.bairro());
            ps.setInt(4, municipio.codigoIbge());
            return ps;
        }, keyHolder);

        if(keyHolder.getKeys() != null && keyHolder.getKeys().get("id") != null) {
            return new Localidade(
                (Integer) keyHolder.getKeys().get("id"),
                localidade.pais(),
                municipio,
                localidade.distrito(),
                localidade.bairro()
            );
        } else {
            throw new IllegalStateException("O banco não retornou o ID gerado para a localidade.");
        }
    }

    public Municipio save(Municipio municipio) {
        final String sql = "INSERT INTO Municipio(codigo_ibge, nome, estado_id) VALUES (?, ?, ?)";

        var estado = save(municipio.estado());
        jdbcTemplate.update(sql, municipio.codigoIbge(), municipio.nome(), estado.id());
        return new Municipio(municipio.codigoIbge(), municipio.nome(), estado);
    }

    public Estado save(Estado estado) {
        final String sql = "INSERT INTO Estado(id, sigla, nome) VALUES (?, ?, ?)";

        jdbcTemplate.update(sql, estado.id(), estado.sigla(), estado.nome());
        return estado;
    }

    public EnderecoResidencial findEnderecoResidencialById(int id) {
        final String sql = "SELECT * FROM EnderecoResidencial WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new EnderecoResidencial(
                rs.getInt("id"),
                findLocalidadeById(rs.getInt("id_localidade")),
                rs.getString("logradouro"),
                rs.getString("codigo_logradouro"),
                rs.getString("numero"),
                rs.getString("complemento"),
                rs.getString("geocampo1"),
                rs.getString("geocampo2"),
                rs.getString("ponto_referencia"),
                rs.getString("cep"),
                rs.getString("telefone"),
                DominioCodificado.fromCodigo(rs.getInt("zona"), Zona.class)
        ), id);
    }

    public Localidade findLocalidadeById(int id) {
        final String sql = "SELECT * FROM Localidade WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Localidade(
                rs.getInt("id"),
                rs.getString("pais"),
                findMunicipioByCodigoIbge(rs.getInt("cidade_codigo_ibge")),
                rs.getString("distrito"),
                rs.getString("bairro")
        ), id);
    }

    public Municipio findMunicipioByCodigoIbge(int codigoIbge) {
        final String sql = "SELECT * FROM Municipio WHERE codigo_ibge = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Municipio(
            rs.getInt("codigo_ibge"),
            rs.getString("nome"),
            findEstadoById(rs.getInt("estado_id"))
        ), codigoIbge);
    }

    public Estado findEstadoById(int id) {
        final String sql = "SELECT id, sigla, nome FROM Estado WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Estado(
                rs.getInt("id"),
                rs.getString("sigla"),
                rs.getString("nome")
        ), id);
    }

    public Estado findEstadoBySigla(String sigla) {
        final String sql = "SELECT id, sigla, nome FROM Estado WHERE sigla = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Estado(
                rs.getInt("id"),
                rs.getString("sigla"),
                rs.getString("nome")
        ), sigla);
    }
}
