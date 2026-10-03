package edu.ifspb.jppgmx.smfin.notificacao;

import edu.ifspb.jppgmx.smfin.DominioCodificado;
import edu.ifspb.jppgmx.smfin.localidade.LocalidadeRepository;
import edu.ifspb.jppgmx.smfin.paciente.PacienteRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    public FichaNotificacao save(FichaNotificacao fichaNotificacao) {
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
            fichaNotificacao.classificacaoFinal() == null ? null : fichaNotificacao.classificacaoFinal().getCodigo(),
            fichaNotificacao.criterioConfirmacaoDescarte() == null ? null : fichaNotificacao.criterioConfirmacaoDescarte().getCodigo(),
            fichaNotificacao.autoctone() == null ? null : fichaNotificacao.autoctone().getCodigo(),
            fichaNotificacao.relacionadoTrabalho() == null ? null : fichaNotificacao.relacionadoTrabalho().getCodigo(),
            fichaNotificacao.evolucaoCaso() == null ? null : fichaNotificacao.evolucaoCaso().getCodigo(),
            fichaNotificacao.dataObito(),
            fichaNotificacao.dataEncerramento(),
            fichaNotificacao.observacoes(),
            municipioNotificado.codigoIbge(),
            fichaNotificacao.unidadeNotificadora().codigo(),
            fichaNotificacao.investigadorResponsavel() == null ? null : fichaNotificacao.investigadorResponsavel().id(),
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
        return jdbcTemplate.query(sql, fichaNotificacaoMapper(), id).stream().findFirst().orElse(null);
    }

    public Page<FichaNotificacao> findAll(FichaNotificacaoFiltro filtro, Pageable pageable) {
        var where = new StringBuilder(" FROM FichaNotificacao f " +
                "LEFT JOIN AgravoDoenca a ON a.cid10 = f.agravo_cid10 " +
                "LEFT JOIN Paciente p ON p.id = f.paciente_id " +
                "LEFT JOIN Unidade u ON u.codigo = f.unidade_notificadora_codigo WHERE 1 = 1");
        var args = new ArrayList<>();
        appendFilters(where, args, filtro);

        String orderBy = pageable.getSort().stream()
                .map(order -> sortColumn(order.getProperty()) + " " + order.getDirection())
                .findFirst().orElse("f.id ASC");
        var count = jdbcTemplate.queryForObject("SELECT COUNT(*)" + where, Long.class, args.toArray());
        var sql = "SELECT f.*" + where + " ORDER BY " + orderBy + " LIMIT ? OFFSET ?";
        args.add(pageable.getPageSize());
        args.add(pageable.getOffset());
        return new PageImpl<>(jdbcTemplate.query(sql, fichaNotificacaoMapper(), args.toArray()),
                pageable, count == null ? 0 : count);
    }

    private void appendFilters(StringBuilder where, List<Object> args, FichaNotificacaoFiltro filtro) {
        if (filtro.q() != null && !filtro.q().isBlank()) {
            where.append(" AND (LOWER(f.id) LIKE LOWER(?) OR LOWER(a.cid10) LIKE LOWER(?) " +
                    "OR LOWER(a.nome) LIKE LOWER(?) OR LOWER(p.nome) LIKE LOWER(?) " +
                    "OR LOWER(u.nome) LIKE LOWER(?))");
            String query = "%" + filtro.q().trim() + "%";
            args.add(query);
            args.add(query);
            args.add(query);
            args.add(query);
            args.add(query);
        }
        if (filtro.tipo() != null) {
            where.append(" AND f.tipo = ?");
            args.add(filtro.tipo());
        }
        if (filtro.agravo() != null && !filtro.agravo().isBlank()) {
            where.append(" AND (LOWER(f.agravo_cid10) = LOWER(?) OR LOWER(a.nome) LIKE LOWER(?))");
            args.add(filtro.agravo().trim());
            args.add("%" + filtro.agravo().trim() + "%");
        }
        if (filtro.unidade() != null && !filtro.unidade().isBlank()) {
            where.append(" AND (LOWER(f.unidade_notificadora_codigo) = LOWER(?) OR LOWER(u.nome) LIKE LOWER(?))");
            args.add(filtro.unidade().trim());
            args.add("%" + filtro.unidade().trim() + "%");
        }
        if (filtro.municipio() != null) {
            where.append(" AND f.municipio_notificacao_codigo_ibge = ?");
            args.add(filtro.municipio());
        }
        if (filtro.dataNotificacaoInicio() != null) {
            where.append(" AND f.data_notificacao >= ?");
            args.add(filtro.dataNotificacaoInicio());
        }
        if (filtro.dataNotificacaoFim() != null) {
            where.append(" AND f.data_notificacao <= ?");
            args.add(filtro.dataNotificacaoFim());
        }
        if (Boolean.TRUE.equals(filtro.duplicata())) {
            where.append("""
                    AND EXISTS (
                        SELECT 1
                        FROM FichaNotificacao f2
                        JOIN Paciente p2 ON p2.id = f2.paciente_id
                        WHERE f2.id <> f.id
                          AND NULLIF(TRIM(f2.agravo_cid10), '') IS NOT NULL
                          AND NULLIF(TRIM(p2.nome), '') IS NOT NULL
                          AND p2.data_nascimento IS NOT NULL
                          AND NULLIF(TRIM(p2.nome_mae), '') IS NOT NULL
                          AND f2.data_notificacao IS NOT NULL
                          AND LOWER(TRIM(f2.agravo_cid10)) = LOWER(TRIM(f.agravo_cid10))
                          AND %s = %s
                          AND p2.data_nascimento = p.data_nascimento
                          AND %s = %s
                          AND ABS(JULIANDAY(f2.data_notificacao) - JULIANDAY(f.data_notificacao)) <= 3
                    )
                    """.formatted(
                    normalizedSql("p2.nome"), normalizedSql("p.nome"),
                    normalizedSql("p2.nome_mae"), normalizedSql("p.nome_mae")));
        }
    }

    private String normalizedSql(String column) {
        return "LOWER(TRIM(REPLACE(REPLACE(REPLACE(" + column
                + ", '  ', ' '), '  ', ' '), '  ', ' ')))";
    }

    private String sortColumn(String property) {
        return switch (property) {
            case "id" -> "f.id";
            case "tipo" -> "f.tipo";
            case "dataNotificacao" -> "f.data_notificacao";
            case "dataInvestigacao" -> "f.data_investigacao";
            case "dataEncerramento" -> "f.data_encerramento";
            case "agravo" -> "a.nome";
            case "unidade" -> "u.nome";
            default -> "f.id";
        };
    }

    public FichaNotificacao update(String id, FichaNotificacao ficha) {
        if (findById(id) == null) {
            return null;
        }
        final String sql = """
                UPDATE FichaNotificacao
                SET tipo = ?, data_notificacao = ?, data_sintoma = ?, data_investigacao = ?,
                    classificacao_final = ?, criterio_cd = ?, caso_autoctone = ?,
                    doenca_relacionada_trabalho = ?, evolucao = ?, data_obito = ?,
                    data_encerramento = ?, observacoes = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, ficha.tipo().getCodigo(), ficha.dataNotificacao(), ficha.dataSintoma(),
                ficha.dataInvestigacao(), ficha.classificacaoFinal() == null ? null : ficha.classificacaoFinal().getCodigo(),
                ficha.criterioConfirmacaoDescarte() == null ? null : ficha.criterioConfirmacaoDescarte().getCodigo(),
                ficha.autoctone() == null ? null : ficha.autoctone().getCodigo(),
                ficha.relacionadoTrabalho() == null ? null : ficha.relacionadoTrabalho().getCodigo(),
                ficha.evolucaoCaso() == null ? null : ficha.evolucaoCaso().getCodigo(),
                ficha.dataObito(), ficha.dataEncerramento(), ficha.observacoes(), id);
        return findById(id);
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
