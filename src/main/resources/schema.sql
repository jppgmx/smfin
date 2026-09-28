/*
========================================
-- Tabelas de localização geográfica
========================================
*/

CREATE TABLE Estado
(
    id    INT PRIMARY KEY,
    sigla VARCHAR(2)   NOT NULL UNIQUE,
    nome  VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Cidade
(
    codigo_ibge INT PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    estado_id   INT          NOT NULL,
    FOREIGN KEY (estado_id) REFERENCES Estado (id)
);

CREATE TABLE Localidade
(
    id                 SERIAL PRIMARY KEY, -- ID sintético
    pais               VARCHAR(200),       -- Paciente pode estar em outro país
    distrito           VARCHAR(200),       -- Opcional
    bairro             VARCHAR(200),       -- Opcional
    cidade_codigo_ibge INT,                -- Opcional, pois pode ser outro país
    FOREIGN KEY (cidade_codigo_ibge) REFERENCES Cidade (codigo_ibge)
);

CREATE TABLE EnderecoResidencial
(
    -- Toda ficha vai fazer cópias dos mesmos dados, pois não é possível reutilizar
    id_localidade     INT PRIMARY KEY,
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
        CHECK (logradouro_codigo IS NULL OR logradouro IS NOT NULL)
);

/*
========================================
-- Tabela do Paciente
========================================
*/

CREATE TABLE Paciente
(
    id              SERIAL PRIMARY KEY,     -- ID sintético
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

    FOREIGN KEY (id_residencial) REFERENCES EnderecoResidencial (id_localidade),
    CONSTRAINT chk_data_ou_idade CHECK (
        (data_nascimento IS NOT NULL AND idade IS NULL AND idade_unidade IS NULL)
            OR (data_nascimento IS NULL AND idade IS NOT NULL AND idade_unidade IS NOT NULL)
        ),
    CONSTRAINT chk_gestante_sexo CHECK (
        gestante IS NOT NULL AND sexo = 'F'
        )
);

/*
========================================
-- Tabelas relacionados a notificações de casos
========================================
*/

CREATE TABLE Unidade
(
    codigo                VARCHAR(20) PRIMARY KEY,
    nome                  VARCHAR(200) NOT NULL,
    tipo                  INT          NOT NULL, -- enum smfin.notificacao.Unidade.Tipo::getCodigo(),
    municipio_codigo_ibge INT          NOT NULL,
    FOREIGN KEY (municipio_codigo_ibge) REFERENCES Cidade (codigo_ibge)
);

CREATE TABLE Investigador
(
    id     SERIAL PRIMARY KEY, -- ID sintético,
    nome   VARCHAR(200) NOT NULL,
    funcao VARCHAR(100) NOT NULL
);

CREATE TABLE UnidadeInvestigador
(
    unidade_codigo  VARCHAR(20) NOT NULL,
    investigador_id INT         NOT NULL,
    PRIMARY KEY (unidade_codigo, investigador_id),
    FOREIGN KEY (unidade_codigo) REFERENCES Unidade (codigo),
    FOREIGN KEY (investigador_id) REFERENCES Investigador (id)
);

CREATE TABLE AgravoDoenca
(
    cid10_codigo VARCHAR(20) PRIMARY KEY,
    nome         VARCHAR(200) NOT NULL
);

CREATE TABLE FichaNotificacao
(
    id                                VARCHAR(50) PRIMARY KEY, -- Cada unidade pode adicionar um ID de preferência.
    tipo                              INT         NOT NULL,    -- enum smfin.notificacao.TipoNotificacao::getCodigo()
    agdo_cid10_codigo                 VARCHAR(20) NOT NULL,
    data_notificacao                  DATE        NOT NULL,
    municipio_notificacao_codigo_ibge INT         NOT NULL,
    unidade_registradora_codigo       VARCHAR(20) NOT NULL,
    dataSintomas                      DATE,                    -- Data de início dos sintomas
    paciente_id                       INT         NOT NULL,
    data_investigacao                 DATE        NOT NULL,
    classificacao_final               INT,                     -- enum smfin.notificacao.ClassificacaoFinal::getCodigo()
    criterico_conf_descarte           INT,                     -- enum smfin.notificacao.CriterioConfirmacao::getCodigo()
    caso_autoctone                    INT,                     -- enum smfin.notificacao.Autoctone::getCodigo()
    local_surto_id                    INT,                     -- Local provável de surto
    doenca_relacionada_trabalho       INT,                     -- enum smfin.notificacao.DoencaRelacionadaTrabalho::getCodigo()
    evolucao_caso                     INT,                     -- enum smfin.notificacao.EvolucaoCaso::getCodigo(),
    data_obito                        DATE,
    data_encerramento                 DATE,
    observacoes                       TEXT,                    -- Observações gerais da investigação
    investigador_responsavel_id       INT,
    assinado DATE, -- Se a ficha foi assinada pelo investigador responsável
    atualizado DATE, -- Data da última atualização da ficha

    FOREIGN KEY (agdo_cid10_codigo) REFERENCES AgravoDoenca (cid10_codigo),
    FOREIGN KEY (municipio_notificacao_codigo_ibge) REFERENCES Cidade (codigo_ibge),
    FOREIGN KEY (unidade_registradora_codigo) REFERENCES Unidade (codigo),
    FOREIGN KEY (paciente_id) REFERENCES Paciente (id),
    FOREIGN KEY (local_surto_id) REFERENCES Localidade (id),
    FOREIGN KEY (investigador_responsavel_id) REFERENCES Investigador (id),
    CONSTRAINT chk_fim_notificacao
        CHECK (
            classificacao_final IS NOT NULL AND data_encerramento IS NOT NULL
        )
)
