package edu.ifspb.jppgmx.smfin.paciente;

import edu.ifspb.jppgmx.smfin.DominioCodificado;
import edu.ifspb.jppgmx.smfin.localidade.LocalidadeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;

@Repository
public class PacienteRepository {
    private final JdbcTemplate jdbcTemplate;
    private final LocalidadeRepository localidadeRepository;

    public PacienteRepository(JdbcTemplate jdbcTemplate, LocalidadeRepository localidadeRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.localidadeRepository = localidadeRepository;
    }

    public Paciente save(Paciente paciente) {
        final String sql = "INSERT INTO  Paciente(nome, data_nascimento, idade, idade_unidade, sexo, gestante, raca_cor, escolaridade, cns, nome_mae, id_residencial) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        var endereco = localidadeRepository.save(paciente.endereco());
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, paciente.nome());
            ps.setObject(2, paciente.dataNascimento());
            ps.setObject(3, paciente.idade());
            ps.setObject(4, paciente.idadeUnidade().getCodigo());
            ps.setObject(5, paciente.sexo().getCodigo());
            ps.setObject(6, paciente.gestante().getCodigo());
            ps.setObject(7, paciente.racaCor().getCodigo());
            ps.setObject(8, paciente.escolaridade().getCodigo());
            ps.setString(9, paciente.cns());
            ps.setString(10, paciente.nomeMae());
            ps.setObject(11, endereco.id());
            return ps;
        }, keyHolder);

        if(keyHolder.getKeys() != null && keyHolder.getKeys().get("id") != null) {
            return new Paciente(
                (Integer) keyHolder.getKeys().get("id"),
                paciente.nome(),
                paciente.dataNascimento(),
                paciente.idade(),
                paciente.idadeUnidade(),
                paciente.sexo(),
                paciente.gestante(),
                paciente.racaCor(),
                paciente.escolaridade(),
                paciente.cns(),
                paciente.nomeMae(),
                endereco
            );
        } else {
            throw new IllegalStateException("O banco não retornou o ID gerado para o paciente.");
        }
    }

    public Paciente findById(int id) {
        final String sql = "SELECT * FROM Paciente WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            var endereco = localidadeRepository.findEnderecoResidencialById(rs.getInt("id_residencial"));
            return new Paciente(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getObject("data_nascimento", java.time.LocalDate.class),
                rs.getObject("idade", Integer.class),
                DominioCodificado.fromCodigo(rs.getObject("idade_unidade", Integer.class), IdadeUnidade.class),
                DominioCodificado.fromCodigo(rs.getString("sexo").charAt(0), Sexo.class),
                DominioCodificado.fromCodigo(rs.getObject("gestante", Integer.class), Gestante.class),
                DominioCodificado.fromCodigo(rs.getObject("raca_cor", Integer.class), RacaCor.class),
                DominioCodificado.fromCodigo(rs.getObject("escolaridade", Integer.class), Escolaridade.class),
                rs.getString("cns"),
                rs.getString("nome_mae"),
                endereco
            );
        }, id);
    }
}
