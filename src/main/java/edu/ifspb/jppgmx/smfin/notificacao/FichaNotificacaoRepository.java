package edu.ifspb.jppgmx.smfin.notificacao;

import edu.ifspb.jppgmx.smfin.DominioCodificado;
import edu.ifspb.jppgmx.smfin.localidade.LocalidadeRepository;
import edu.ifspb.jppgmx.smfin.paciente.PacienteRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class FichaNotificacaoRepository {
    private final JdbcTemplate jdbcTemplate;
    private final PacienteRepository pacienteRepository;
    private final LocalidadeRepository localidadeRepository;
    private final UnidadeRepository unidadeRepository;
    private final InvestigadorRepository investigadorRepository;
    private final AgravoDoencaRepository agravoDoencaRepository;

    public FichaNotificacaoRepository(JdbcTemplate jdbcTemplate, PacienteRepository pacienteRepository,
                                      LocalidadeRepository localidadeRepository, UnidadeRepository unidadeRepository,
                                      InvestigadorRepository investigadorRepository, AgravoDoencaRepository agravoDoencaRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.pacienteRepository = pacienteRepository;
        this.localidadeRepository = localidadeRepository;
        this.unidadeRepository = unidadeRepository;
        this.investigadorRepository = investigadorRepository;
        this.agravoDoencaRepository = agravoDoencaRepository;
    }

    public FichaNotificacao salvar(FichaNotificacao fichaNotificacao) {
        final String sql = "INSERT INTO FichaNotificacao(" +
                "id, tipo, data_notificacao, data_sintoma, data_investigacao, classificacao_final, " +
                "criterio_cd, caso_autoctone, doenca_relacionada_trabalho, evolucao, data_obito, " +
                "data_encerramento, observacoes, municipio_notificacao_codigo_ibge, " +
                "unidade_notificadora_codigo, investigador_responsavel_id, agravo_cid10, paciente_id, " +
                "local_provavel_id" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        var municipioNotificado = localidadeRepository.save(fichaNotificacao.municipioNotificado());
        var paciente = fichaNotificacao.paciente() == null ? null :
                pacienteRepository.save(fichaNotificacao.paciente());
        var localProvavelInfeccao = fichaNotificacao.localProvavelInfeccao() == null
                ? null : localidadeRepository.save(fichaNotificacao.localProvavelInfeccao());

        var id = String.format("%s-%d", fichaNotificacao.unidadeNotificadora().codigo(), System.currentTimeMillis());

        jdbcTemplate.update(sql,
            id,
            fichaNotificacao.tipo().getCodigo(),
            fichaNotificacao.dataNotificacao(),
            fichaNotificacao.dataSintoma(),
            fichaNotificacao.dataInvestigacao(),
            fichaNotificacao.classificacaoFinal().getCodigo(),
            fichaNotificacao.criterioConfirmacaoDescarte().getCodigo(),
            fichaNotificacao.autoctone().getCodigo(),
            fichaNotificacao.relacionadoTrabalho().getCodigo(),
            fichaNotificacao.evolucaoCaso().getCodigo(),
            fichaNotificacao.dataObito(),
            fichaNotificacao.dataEncerramento(),
            fichaNotificacao.observacoes(),
            municipioNotificado.codigoIbge(),
            fichaNotificacao.unidadeNotificadora().codigo(),
            fichaNotificacao.investigadorResponsavel().id(),
            fichaNotificacao.agravoDoenca().cid10(),
            paciente == null ? null : paciente.id(),
            localProvavelInfeccao == null ? null : localProvavelInfeccao.id()
        );

        return new FichaNotificacao(
            id,
            fichaNotificacao.tipo(),
            fichaNotificacao.agravoDoenca(),
            fichaNotificacao.dataNotificacao(),
            municipioNotificado,
            fichaNotificacao.unidadeNotificadora(),
            fichaNotificacao.dataSintoma(),
            paciente,
            fichaNotificacao.dataInvestigacao(),
            fichaNotificacao.classificacaoFinal(),
            fichaNotificacao.criterioConfirmacaoDescarte(),
            fichaNotificacao.autoctone(),
            localProvavelInfeccao,
            fichaNotificacao.relacionadoTrabalho(),
            fichaNotificacao.evolucaoCaso(),
            fichaNotificacao.dataObito(),
            fichaNotificacao.dataEncerramento(),
            fichaNotificacao.observacoes(),
            fichaNotificacao.investigadorResponsavel()
        );
    }

    public FichaNotificacao findById(String id) {
        final String sql = "SELECT * FROM FichaNotificacao WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, fichaNotificacaoMapper(), id);
    }

    public List<FichaNotificacao> findByUnidade(Unidade unidade) {
        final String sql = "SELECT * FROM FichaNotificacao WHERE unidade_notificadora_codigo = ?";

        return jdbcTemplate.query(sql, fichaNotificacaoMapper(), unidade.codigo());
    }

    public List<FichaNotificacao> findAll() {
        final String sql = "SELECT * FROM FichaNotificacao";

        return jdbcTemplate.query(sql, fichaNotificacaoMapper());
    }

    @Transactional
    public boolean deleteById(String id) {
        Optional<DependenciasFicha> dependencias = findDependencias(id);
        if (dependencias.isEmpty()) {
            return false;
        }

        DependenciasFicha dados = dependencias.get();
        jdbcTemplate.update("DELETE FROM FichaNotificacao WHERE id = ?", id);

        if (dados.pacienteId() != null) {
            jdbcTemplate.update("DELETE FROM Paciente WHERE id = ?", dados.pacienteId());
        }
        if (dados.enderecoId() != null) {
            jdbcTemplate.update("DELETE FROM EnderecoResidencial WHERE id = ?", dados.enderecoId());
        }
        if (dados.localidadeEnderecoId() != null) {
            jdbcTemplate.update("DELETE FROM Localidade WHERE id = ?", dados.localidadeEnderecoId());
        }
        if (dados.localidadeProvavelId() != null
                && !dados.localidadeProvavelId().equals(dados.localidadeEnderecoId())) {
            jdbcTemplate.update("DELETE FROM Localidade WHERE id = ?", dados.localidadeProvavelId());
        }

        return true;
    }

    private Optional<DependenciasFicha> findDependencias(String id) {
        final String sql = """
                SELECT f.paciente_id,
                       p.endereco_residencial_id,
                       e.localidade_id AS localidade_endereco_id,
                       f.local_provavel_id
                FROM FichaNotificacao f
                LEFT JOIN Paciente p ON p.id = f.paciente_id
                LEFT JOIN EnderecoResidencial e ON e.id = p.endereco_residencial_id
                WHERE f.id = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new DependenciasFicha(
                (Integer) rs.getObject("paciente_id"),
                (Integer) rs.getObject("endereco_residencial_id"),
                (Integer) rs.getObject("localidade_endereco_id"),
                (Integer) rs.getObject("local_provavel_id")
        ), id).stream().findFirst();
    }

    private record DependenciasFicha(
            Integer pacienteId,
            Integer enderecoId,
            Integer localidadeEnderecoId,
            Integer localidadeProvavelId
    ) {
    }

    private @NonNull RowMapper<FichaNotificacao> fichaNotificacaoMapper() {
        return (rs, rowNum) -> {
            var municipioNotificado = localidadeRepository.findMunicipioByCodigoIbge(rs.getInt("municipio_notificacao_codigo_ibge"));
            var paciente = rs.getObject("paciente_id") == null ? null :
                    pacienteRepository.findById(rs.getInt("paciente_id"));
            var localProvavelInfeccao = rs.getObject("local_provavel_id") == null ? null :
                    localidadeRepository.findLocalidadeById(rs.getInt("local_provavel_id"));
            var investigadorResponsavel = rs.getObject("investigador_responsavel_id") == null ? null :
                    investigadorRepository.findById(rs.getInt("investigador_responsavel_id"));
            var agravoDoenca = agravoDoencaRepository.findByCid10(rs.getString("agravo_cid10"));

            return new FichaNotificacao(
                    rs.getString("id"),
                    DominioCodificado.fromCodigo(rs.getInt("tipo"), TipoNotificacao.class),
                    agravoDoenca,
                    rs.getDate("data_notificacao").toLocalDate(),
                    municipioNotificado,
                    unidadeRepository.findByCodigo(rs.getString("unidade_notificadora_codigo")),
                    rs.getDate("data_sintoma") == null ? null : rs.getDate("data_sintoma").toLocalDate(),
                    paciente,
                    rs.getDate("data_investigacao").toLocalDate(),
                    DominioCodificado.fromCodigo((Integer) rs.getObject("classificacao_final"), ClassificacaoFinal.class),
                    DominioCodificado.fromCodigo((Integer) rs.getObject("criterio_cd"), CriterioConfirmacao.class),
                    DominioCodificado.fromCodigo((Integer) rs.getObject("caso_autoctone"), Autoctone.class),
                    localProvavelInfeccao,
                    DominioCodificado.fromCodigo((Integer) rs.getObject("doenca_relacionada_trabalho"), DoencaRelacionadaTrabalho.class),
                    DominioCodificado.fromCodigo((Integer) rs.getObject("evolucao"), EvolucaoCaso.class),
                    rs.getDate("data_obito") != null ? rs.getDate("data_obito").toLocalDate() : null,
                    rs.getDate("data_encerramento") != null ? rs.getDate("data_encerramento").toLocalDate() : null,
                    rs.getString("observacoes"),
                    investigadorResponsavel
            );
        };
    }
}
