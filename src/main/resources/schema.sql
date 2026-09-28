/*
========================================
-- Tabelas de localização geográfica
========================================
*/

CREATE TABLE IF NOT EXISTS Estado
(
    id    INT PRIMARY KEY,
    sigla VARCHAR(2)   NOT NULL UNIQUE,
    nome  VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Cidade
(
    codigo_ibge INT PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    estado_id   INT          NOT NULL,
    FOREIGN KEY (estado_id) REFERENCES Estado (id)
);

CREATE TABLE IF NOT EXISTS Localidade
(
    id                 INTEGER PRIMARY KEY AUTOINCREMENT, -- ID sintético
    pais               VARCHAR(200),       -- Paciente pode estar em outro país
    distrito           VARCHAR(200),       -- Opcional
    bairro             VARCHAR(200),       -- Opcional
    cidade_codigo_ibge INT,                -- Opcional, pois pode ser outro país
    FOREIGN KEY (cidade_codigo_ibge) REFERENCES Cidade (codigo_ibge)
);

CREATE TABLE IF NOT EXISTS EnderecoResidencial
(
    -- Toda ficha vai fazer cópias dos mesmos dados, pois não é possível reutilizar
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    id_localidade     INT NOT NULL,
    logradouro        VARCHAR(200),
    logradouro_codigo VARCHAR(20),  -- Código do logradouro, se houver
    numero            VARCHAR(20),  -- NULL é sem número,
    complemento       VARCHAR(200),
    geocampo1         VARCHAR(200), -- Campo de geolocalização 1
    geocampo2         VARCHAR(200), -- Campo de geolocalização 2
    pontoReferencia   VARCHAR(200), -- Ponto de referência
    cep               VARCHAR(10),  -- XXXXX-XXX
    zona              INT,          -- enum smfin.localidade.Zona::getCodigo()
    telefone          VARCHAR(20),

    FOREIGN KEY (id_localidade) REFERENCES Localidade (id),

    -- Faz sentido preencher o código do logradouro quando logradouro está preenchido.
    CONSTRAINT chk_logradouro_codigo
        CHECK (logradouro_codigo IS NULL OR logradouro IS NOT NULL),
    CONSTRAINT chk_zona CHECK (zona IS NULL OR zona IN (1, 2, 3, 9)),
    CONSTRAINT chk_cep CHECK (cep IS NULL OR length(replace(cep, '-', '')) = 8)
);

/*
========================================
-- Tabela do Paciente
========================================
*/

CREATE TABLE IF NOT EXISTS Paciente
(
    id              INTEGER PRIMARY KEY AUTOINCREMENT,     -- ID sintético
    nome            VARCHAR(200) NOT NULL,
    data_nascimento DATE,                   -- Se não souber, preencha as colunas abaixo
    idade           INT,                    -- Idade aproximada, se não souber a data de nascimento
    idade_unidade   INT,                    -- enum smfin.paciente.IdadeUnidade::getCodigo()
    sexo            CHAR(1)      NOT NULL,  -- enum smfin.paciente.Sexo::getCodigo()
    gestante        INT,                    -- enum smfin.paciente.Gestante::getCodigo()
    raca_cor        INT,                    -- enum smfin.paciente.RacaCor::getCodigo()
    escolaridade    INT,                    -- enum smfin.paciente.Escolaridade::getCodigo()

    -- É contraintuitivo não marcar como UNIQUE. O problema é que como não há reutilização de dados,
    -- o mesmo paciente pode ser cadastrado várias vezes com o mesmo CNS.
    cns             VARCHAR(15) /*UNIQUE*/, -- Cartão Nacional de Saúde
    nome_mae        VARCHAR(200),
    id_residencial  INT,                    -- FK para EnderecoResidencial

    FOREIGN KEY (id_residencial) REFERENCES EnderecoResidencial (id),
    CONSTRAINT chk_data_ou_idade CHECK (
        (data_nascimento IS NOT NULL AND idade IS NULL AND idade_unidade IS NULL)
            OR (data_nascimento IS NULL AND idade IS NOT NULL AND idade_unidade IS NOT NULL)
        ),
    CONSTRAINT chk_gestante_sexo CHECK (
        (sexo = 'F' AND gestante IS NOT NULL)
            OR (sexo IN ('M', 'I') AND gestante IS NULL)
        ),
    CONSTRAINT chk_sexo CHECK (sexo IN ('M', 'F', 'I')),
    CONSTRAINT chk_idade CHECK (idade IS NULL OR idade >= 0),
    CONSTRAINT chk_idade_unidade CHECK (idade_unidade IS NULL OR idade_unidade IN (1, 2, 3, 4)),
    CONSTRAINT chk_gestante CHECK (gestante IS NULL OR gestante IN (1, 2, 3, 4, 5, 6, 9)),
    CONSTRAINT chk_raca_cor CHECK (raca_cor IS NULL OR raca_cor IN (1, 2, 3, 4, 5, 9)),
    CONSTRAINT chk_escolaridade CHECK (
        escolaridade IS NULL OR escolaridade BETWEEN 0 AND 10
    )
);

/*
========================================
-- Tabelas relacionados a notificações de casos
========================================
*/

CREATE TABLE IF NOT EXISTS Unidade
(
    codigo                VARCHAR(20) PRIMARY KEY,
    nome                  VARCHAR(200) NOT NULL,
    tipo                  INT          NOT NULL, -- enum smfin.notificacao.Unidade.Tipo::getCodigo(),
    municipio_codigo_ibge INT          NOT NULL,
    FOREIGN KEY (municipio_codigo_ibge) REFERENCES Cidade (codigo_ibge)
);

CREATE TABLE IF NOT EXISTS Investigador
(
    id     INTEGER PRIMARY KEY AUTOINCREMENT, -- ID sintético,
    nome   VARCHAR(200) NOT NULL,
    funcao VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS UnidadeInvestigador
(
    unidade_codigo  VARCHAR(20) NOT NULL,
    investigador_id INT         NOT NULL,
    PRIMARY KEY (unidade_codigo, investigador_id),
    FOREIGN KEY (unidade_codigo) REFERENCES Unidade (codigo),
    FOREIGN KEY (investigador_id) REFERENCES Investigador (id)
);

CREATE TABLE IF NOT EXISTS AgravoDoenca
(
    cid10_codigo VARCHAR(20) PRIMARY KEY,
    nome         VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS FichaNotificacao
(
    id                                VARCHAR(50) PRIMARY KEY, -- Cada unidade pode adicionar um ID de preferência.
    tipo                              INT         NOT NULL,    -- enum smfin.notificacao.TipoNotificacao::getCodigo()
    agdo_cid10_codigo                 VARCHAR(20) NOT NULL,
    data_notificacao                  DATE        NOT NULL,
    municipio_notificacao_codigo_ibge INT         NOT NULL,
    unidade_registradora_codigo       VARCHAR(20) NOT NULL,
    dataSintomas                      DATE,                    -- Data de início dos sintomas
    paciente_id                       INT,
    endereco_residencial_id           INT,
    data_investigacao                 DATE        NOT NULL,
    classificacao_final               INT,                     -- enum smfin.notificacao.ClassificacaoFinal::getCodigo()
    criterico_conf_descarte           INT,                     -- enum smfin.notificacao.CriterioConfirmacao::getCodigo()
    caso_autoctone                    INT,                     -- enum smfin.notificacao.Autoctone::getCodigo()
    local_surto_id                    INT,                     -- Local provável de surto
    local_provavel_infeccao_id        INT,                     -- Campos 35 a 39
    doenca_relacionada_trabalho       INT,                     -- enum smfin.notificacao.DoencaRelacionadaTrabalho::getCodigo()
    evolucao_caso                     INT,                     -- enum smfin.notificacao.EvolucaoCaso::getCodigo(),
    data_obito                        DATE,
    data_encerramento                 DATE,
    observacoes                       TEXT,                    -- Observações gerais da investigação
    investigador_responsavel_id       INT,
    unidade_investigadora_codigo      VARCHAR(20),
    assinatura_responsavel            TEXT,
    assinado_em                       DATE,
    atualizado_em                     DATE,

    FOREIGN KEY (agdo_cid10_codigo) REFERENCES AgravoDoenca (cid10_codigo),
    FOREIGN KEY (municipio_notificacao_codigo_ibge) REFERENCES Cidade (codigo_ibge),
    FOREIGN KEY (unidade_registradora_codigo) REFERENCES Unidade (codigo),
    FOREIGN KEY (paciente_id) REFERENCES Paciente (id),
    FOREIGN KEY (endereco_residencial_id) REFERENCES EnderecoResidencial (id),
    FOREIGN KEY (local_surto_id) REFERENCES Localidade (id),
    FOREIGN KEY (local_provavel_infeccao_id) REFERENCES Localidade (id),
    FOREIGN KEY (investigador_responsavel_id) REFERENCES Investigador (id),
    FOREIGN KEY (unidade_investigadora_codigo) REFERENCES Unidade (codigo),
    CONSTRAINT chk_tipo CHECK (tipo IN (1, 2, 3, 4)),
    CONSTRAINT chk_tipo_paciente_endereco CHECK (
        (tipo = 1 AND paciente_id IS NULL AND endereco_residencial_id IS NULL)
        OR (tipo = 2 AND paciente_id IS NOT NULL AND endereco_residencial_id IS NOT NULL
            AND dataSintomas IS NOT NULL AND local_surto_id IS NULL)
        OR (tipo = 3 AND paciente_id IS NULL AND endereco_residencial_id IS NOT NULL
            AND local_surto_id IS NOT NULL)
        OR (tipo = 4 AND paciente_id IS NULL AND endereco_residencial_id IS NULL)
    ),
    CONSTRAINT chk_classificacao CHECK (
        classificacao_final IS NULL OR classificacao_final IN (1, 2)
    ),
    CONSTRAINT chk_criterio CHECK (
        criterico_conf_descarte IS NULL OR criterico_conf_descarte IN (1, 2)
    ),
    CONSTRAINT chk_classificacao_encerramento CHECK (
        (classificacao_final IS NULL AND data_encerramento IS NULL)
        OR (classificacao_final IS NOT NULL AND data_encerramento IS NOT NULL)
    ),
    CONSTRAINT chk_confirmacao_autoctonia CHECK (
        (classificacao_final IS NULL AND caso_autoctone IS NULL
            AND local_provavel_infeccao_id IS NULL)
        OR (classificacao_final = 1 AND caso_autoctone IS NOT NULL
            AND (caso_autoctone <> 2 OR local_provavel_infeccao_id IS NOT NULL))
        OR (classificacao_final = 2 AND caso_autoctone IS NULL
            AND local_provavel_infeccao_id IS NULL)
    ),
    CONSTRAINT chk_autoctone CHECK (caso_autoctone IS NULL OR caso_autoctone IN (1, 2, 3)),
    CONSTRAINT chk_evolucao CHECK (evolucao_caso IS NULL OR evolucao_caso IN (1, 2, 3, 9)),
    CONSTRAINT chk_trabalho CHECK (
        doenca_relacionada_trabalho IS NULL OR doenca_relacionada_trabalho IN (1, 2, 9)
    ),
    CONSTRAINT chk_datas CHECK (
        data_investigacao IS NULL OR data_investigacao >= data_notificacao
    ),
    CONSTRAINT chk_obito CHECK (
        data_obito IS NULL OR data_encerramento IS NULL OR data_obito <= data_encerramento
    ),
    CONSTRAINT chk_atualizacao CHECK (
        atualizado_em IS NULL OR atualizado_em >= data_notificacao
    )
);
