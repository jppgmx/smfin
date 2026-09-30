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
                "id, tipo, agdo_cid10_codigo, data_notificacao, municipio_notificacao_codigo_ibge, " +
                "unidade_registradora_codigo, dataSintomas, paciente_id, endereco_residencial_id, " +
                "data_investigacao, classificacao_final, criterico_conf_descarte, caso_autoctone, " +
                "local_provavel_infeccao_id, doenca_relacionada_trabalho, evolucao_caso, " +
                "data_obito, data_encerramento, observacoes, investigador_responsavel_id, " +
                "assinatura_responsavel, assinado_em, atualizado_em" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        var municipioNotificado = localidadeRepository.save(fichaNotificacao.municipioNotificado());
        var paciente = pacienteRepository.save(fichaNotificacao.paciente());
        var enderecoResidencial = paciente.endereco();
        var localProvavelInfeccao = localidadeRepository.save(fichaNotificacao.localProvavelInfeccao());

        var id = String.format("%s-%d", fichaNotificacao.unidadeNotificadora().codigo(), System.currentTimeMillis());

        jdbcTemplate.update(sql,
            id,
            fichaNotificacao.tipo().getCodigo(),
            fichaNotificacao.agravoDoenca().cid10(),
            fichaNotificacao.dataNotificacao(),
            municipioNotificado.codigoIbge(),
            fichaNotificacao.unidadeNotificadora().codigo(),
            fichaNotificacao.dataSintomas(),
            paciente.id(),
            enderecoResidencial.id(),
            fichaNotificacao.dataInvestigacao(),
            fichaNotificacao.classificacaoFinal().getCodigo(),
            fichaNotificacao.criterioConfirmacaoDescarte().getCodigo(),
            fichaNotificacao.autoctone().getCodigo(),
            localProvavelInfeccao.id(),
            fichaNotificacao.relacionadoTrabalho().getCodigo(),
            fichaNotificacao.evolucaoCaso().getCodigo(),
            fichaNotificacao.dataObito(),
            fichaNotificacao.dataEncerramento(),
            fichaNotificacao.observacoes(),
            fichaNotificacao.investigadorResponsavel().id(),
            fichaNotificacao.assinaturaResponsavel(),
            fichaNotificacao.assinadoEm(),
            fichaNotificacao.atualizadoEm()
        );

        return new FichaNotificacao(
            id,
            fichaNotificacao.tipo(),
            fichaNotificacao.agravoDoenca(),
            fichaNotificacao.dataNotificacao(),
            municipioNotificado,
            fichaNotificacao.unidadeNotificadora(),
            fichaNotificacao.dataSintomas(),
            paciente,
            enderecoResidencial,
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
            fichaNotificacao.investigadorResponsavel(),
            fichaNotificacao.assinaturaResponsavel(),
            fichaNotificacao.assinadoEm(),
            fichaNotificacao.atualizadoEm()
        );
    }

    public FichaNotificacao findById(String id) {
        final String sql = "SELECT * FROM FichaNotificacao WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, fichaNotificacaoMapper(), id);
    }

    public List<FichaNotificacao> findByUnidade(Unidade unidade) {
        final String sql = "SELECT * FROM FichaNotificacao WHERE unidade_registradora_codigo = ?";

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
                       f.endereco_residencial_id,
                       e.id_localidade AS localidade_endereco_id,
                       f.local_provavel_infeccao_id
                FROM FichaNotificacao f
                LEFT JOIN EnderecoResidencial e
                       ON e.id = f.endereco_residencial_id
                WHERE f.id = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new DependenciasFicha(
                (Integer) rs.getObject("paciente_id"),
                (Integer) rs.getObject("endereco_residencial_id"),
                (Integer) rs.getObject("localidade_endereco_id"),
                (Integer) rs.getObject("local_provavel_infeccao_id")
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
            var paciente = pacienteRepository.findById(rs.getInt("paciente_id"));
            var enderecoResidencial = paciente.endereco();
            var localProvavelInfeccao = localidadeRepository.findLocalidadeById(rs.getInt("local_provavel_infeccao_id"));
            var investigadorResponsavel = investigadorRepository.findById(rs.getInt("investigador_responsavel_id"));
            var agravoDoenca = agravoDoencaRepository.findByCid10(rs.getString("agdo_cid10_codigo"));

            return new FichaNotificacao(
                    rs.getString("id"),
                    DominioCodificado.fromCodigo(rs.getInt("tipo"), TipoNotificacao.class),
                    agravoDoenca,
                    rs.getDate("data_notificacao").toLocalDate(),
                    municipioNotificado,
                    unidadeRepository.findByCodigo(rs.getString("unidade_registradora_codigo")),
                    rs.getDate("dataSintomas").toLocalDate(),
                    paciente,
                    enderecoResidencial,
                    rs.getDate("data_investigacao").toLocalDate(),
                    DominioCodificado.fromCodigo(rs.getInt("classificacao_final"), ClassificacaoFinal.class),
                    DominioCodificado.fromCodigo(rs.getInt("criterico_conf_descarte"), CriterioConfirmacao.class),
                    DominioCodificado.fromCodigo(rs.getInt("caso_autoctone"), Autoctone.class),
                    localProvavelInfeccao,
                    DominioCodificado.fromCodigo(rs.getInt("doenca_relacionada_trabalho"), DoencaRelacionadaTrabalho.class),
                    DominioCodificado.fromCodigo(rs.getInt("evolucao_caso"), EvolucaoCaso.class),
                    rs.getDate("data_obito") != null ? rs.getDate("data_obito").toLocalDate() : null,
                    rs.getDate("data_encerramento") != null ? rs.getDate("data_encerramento").toLocalDate() : null,
                    rs.getString("observacoes"),
                    investigadorResponsavel,
                    rs.getString("assinatura_responsavel"),
                    rs.getDate("assinado_em") != null ? rs.getDate("assinado_em").toLocalDate() : null,
                    rs.getDate("atualizado_em") != null ? rs.getDate("atualizado_em").toLocalDate() : null
            );
        };
    }
}
