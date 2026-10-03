/*
==============================================================================
-- 1. LOCALIZAÇÃO GEOGRÁFICA
==============================================================================
*/

CREATE TABLE IF NOT EXISTS Estado
(
    id    INT PRIMARY KEY,
    sigla VARCHAR(2)   NOT NULL UNIQUE,
    nome  VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Municipio
(
    codigo_ibge INT PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    estado_id   INT          NOT NULL,
    FOREIGN KEY (estado_id) REFERENCES Estado (id)
);

-- Superclasse que atende tanto ao LPI quanto à base do Endereço Residencial
CREATE TABLE IF NOT EXISTS Localidade
(
    id                    INTEGER PRIMARY KEY AUTOINCREMENT,
    pais                  VARCHAR(200),
    distrito              VARCHAR(200),
    bairro                VARCHAR(200),
    municipio_codigo_ibge INT,
    FOREIGN KEY (municipio_codigo_ibge) REFERENCES Municipio (codigo_ibge)
);

-- Especialização de Localidade (herança via chave primária/estrangeira)
CREATE TABLE IF NOT EXISTS EnderecoResidencial
(
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    localidade_id     INT NOT NULL UNIQUE,
    logradouro        VARCHAR(200),
    codigo_logradouro VARCHAR(20),
    numero            VARCHAR(20),
    complemento       VARCHAR(200),
    geocampo1         VARCHAR(200),
    geocampo2         VARCHAR(200),
    ponto_referencia  VARCHAR(200),
    cep               VARCHAR(10),
    zona              INT,
    telefone          VARCHAR(20),

    FOREIGN KEY (localidade_id) REFERENCES Localidade (id) ON DELETE CASCADE,
    CONSTRAINT chk_logradouro_codigo CHECK (codigo_logradouro IS NULL OR logradouro IS NOT NULL),
    CONSTRAINT chk_zona CHECK (zona IS NULL OR zona IN (1, 2, 3, 9)),
    CONSTRAINT chk_cep CHECK (cep IS NULL OR length(replace(cep, '-', '')) = 8)
);

/*
==============================================================================
-- 2. DADOS DO PACIENTE
==============================================================================
*/

CREATE TABLE IF NOT EXISTS Paciente
(
    id                      INTEGER PRIMARY KEY AUTOINCREMENT,
    nome                    VARCHAR(200) NOT NULL,
    nome_mae                VARCHAR(200),
    numero_cns              VARCHAR(15),
    data_nascimento         DATE,
    idade                   INT,
    idade_unidade           INT,
    sexo                    CHAR(1)      NOT NULL,
    gestante                INT,
    raca_cor                INT,
    escolaridade            INT,
    endereco_residencial_id INT          NOT NULL,

    FOREIGN KEY (endereco_residencial_id) REFERENCES EnderecoResidencial (id),
    CONSTRAINT chk_data_ou_idade CHECK (
        (data_nascimento IS NOT NULL AND idade IS NULL AND idade_unidade IS NULL)
            OR (data_nascimento IS NULL AND idade IS NOT NULL AND idade_unidade IS NOT NULL)
        ),
    CONSTRAINT chk_sexo CHECK (sexo IN ('M', 'F', 'I')),
    CONSTRAINT chk_gestante_sexo CHECK (
        (sexo = 'F' AND gestante IS NOT NULL)
            OR (sexo IN ('M', 'I') AND gestante IS NULL)
        ),
    CONSTRAINT chk_idade CHECK (idade IS NULL OR idade >= 0),
    CONSTRAINT chk_idade_unidade CHECK (idade_unidade IS NULL OR idade_unidade IN (1, 2, 3, 4)),
    CONSTRAINT chk_gestante CHECK (gestante IS NULL OR gestante IN (1, 2, 3, 4, 5, 6, 9, 10)),
    CONSTRAINT chk_raca_cor CHECK (raca_cor IS NULL OR raca_cor IN (1, 2, 3, 4, 5, 9)),
    CONSTRAINT chk_escolaridade CHECK (escolaridade IS NULL OR escolaridade BETWEEN 0 AND 10)
);

/*
==============================================================================
-- 3. UNIDADES, INVESTIGADOR E AGRAVO
==============================================================================
*/

CREATE TABLE IF NOT EXISTS Unidade
(
    codigo                VARCHAR(20) PRIMARY KEY,
    nome                  VARCHAR(200) NOT NULL,
    tipo                  INT          NOT NULL,
    municipio_codigo_ibge INT          NOT NULL,
    FOREIGN KEY (municipio_codigo_ibge) REFERENCES Municipio (codigo_ibge)
);

-- Relacionamento 1:N entre Investigador (1,1) e Unidade (1,n)
CREATE TABLE IF NOT EXISTS Investigador
(
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    nome           VARCHAR(200) NOT NULL,
    funcao         VARCHAR(100) NOT NULL,
    unidade_codigo VARCHAR(20)  NOT NULL,
    FOREIGN KEY (unidade_codigo) REFERENCES Unidade (codigo)
);

CREATE TABLE IF NOT EXISTS AgravoDoenca
(
    cid10 VARCHAR(20) PRIMARY KEY,
    nome  VARCHAR(200) NOT NULL
);

/*
==============================================================================
-- 4. FICHA DE NOTIFICAÇÃO / CONCLUSÃO
==============================================================================
*/

CREATE TABLE IF NOT EXISTS FichaNotificacao
(
    id                                VARCHAR(50) PRIMARY KEY,
    tipo                              INT         NOT NULL,
    data_notificacao                  DATE        NOT NULL,
    data_sintoma                      DATE,
    data_investigacao                 DATE        NOT NULL,
    classificacao_final               INT,
    criterio_cd                       INT,
    caso_autoctone                    INT,
    doenca_relacionada_trabalho       INT,
    evolucao                          INT,
    data_obito                        DATE,
    data_encerramento                 DATE,
    observacoes                       TEXT,

    -- Chaves Estrangeiras refletindo os relacionamentos do DER
    municipio_notificacao_codigo_ibge INT         NOT NULL, -- notificado_no
    unidade_notificadora_codigo       VARCHAR(20) NOT NULL, -- registrado_pela
    investigador_responsavel_id       INT,                  -- registra
    agravo_cid10                      VARCHAR(20) NOT NULL, -- notifica
    paciente_id                       INT,                  -- declara
    local_provavel_id                 INT,                  -- localProvavel

    FOREIGN KEY (municipio_notificacao_codigo_ibge) REFERENCES Municipio (codigo_ibge),
    FOREIGN KEY (unidade_notificadora_codigo) REFERENCES Unidade (codigo),
    FOREIGN KEY (investigador_responsavel_id) REFERENCES Investigador (id),
    FOREIGN KEY (agravo_cid10) REFERENCES AgravoDoenca (cid10),
    FOREIGN KEY (paciente_id) REFERENCES Paciente (id),
    FOREIGN KEY (local_provavel_id) REFERENCES Localidade (id),

    CONSTRAINT chk_tipo CHECK (tipo IN (1, 2, 3, 4)),
    CONSTRAINT chk_tipo_paciente CHECK (
        (tipo = 2 AND paciente_id IS NOT NULL AND data_sintoma IS NOT NULL)
            OR (tipo IN (1, 3, 4) AND paciente_id IS NULL)
        ),
    CONSTRAINT chk_classificacao CHECK (classificacao_final IS NULL OR classificacao_final IN (1, 2)),
    CONSTRAINT chk_criterio CHECK (criterio_cd IS NULL OR criterio_cd IN (1, 2)),
    CONSTRAINT chk_classificacao_encerramento CHECK (
        (classificacao_final IS NULL AND data_encerramento IS NULL)
            OR (classificacao_final IS NOT NULL AND data_encerramento IS NOT NULL)
        ),
    CONSTRAINT chk_confirmacao_autoctonia CHECK (
        (classificacao_final IS NULL AND caso_autoctone IS NULL AND local_provavel_id IS NULL)
            OR (classificacao_final = 1 AND caso_autoctone IS NOT NULL AND (caso_autoctone <> 2 OR local_provavel_id IS NOT NULL))
            OR (classificacao_final = 2 AND caso_autoctone IS NULL AND local_provavel_id IS NULL)
        ),
    CONSTRAINT chk_autoctone CHECK (caso_autoctone IS NULL OR caso_autoctone IN (1, 2, 3)),
    CONSTRAINT chk_evolucao CHECK (evolucao IS NULL OR evolucao IN (1, 2, 3, 9)),
    CONSTRAINT chk_trabalho CHECK (doenca_relacionada_trabalho IS NULL OR doenca_relacionada_trabalho IN (1, 2, 9)),
    CONSTRAINT chk_datas CHECK (data_investigacao >= data_notificacao),
    CONSTRAINT chk_obito CHECK (data_obito IS NULL OR data_encerramento IS NULL OR data_obito <= data_encerramento)
);

/*
==============================================================================
-- 5. CARGAS INICIAIS DE TESTE
==============================================================================
*/

INSERT INTO Estado (id, sigla, nome)
VALUES (25, 'PB', 'Paraíba')
ON CONFLICT(id) DO NOTHING;

INSERT INTO Municipio (codigo_ibge, nome, estado_id) VALUES
    (2507507, 'João Pessoa', 25),
    (2504009, 'Campina Grande', 25),
    (2513703, 'Santa Rita', 25),
    (2510808, 'Patos', 25),
    (2503704, 'Cajazeiras', 25)
ON CONFLICT(codigo_ibge) DO NOTHING;

INSERT INTO Unidade (codigo, nome, tipo, municipio_codigo_ibge) VALUES
    ('U-PB-001', 'UBS Jardim do Sol', 1, 2507507),
    ('U-PB-002', 'Hospital Regional Serra Azul', 2, 2504009),
    ('U-PB-003', 'UPA Santa Rita Norte', 3, 2513703),
    ('U-PB-004', 'Clínica Integrada do Sertão', 4, 2510808),
    ('U-PB-005', 'UBS Cajazeiras Centro', 1, 2503704)
ON CONFLICT(codigo) DO NOTHING;

INSERT INTO Investigador (id, nome, funcao, unidade_codigo) VALUES
    (1001, 'Ana Beatriz Moura', 'Enfermeira sanitarista', 'U-PB-001'),
    (1002, 'Bruno Henrique Lima', 'Médico epidemiologista', 'U-PB-002'),
    (1003, 'Carla Renata Alves', 'Técnica de vigilância epidemiológica', 'U-PB-003'),
    (1004, 'Diego Ferreira Costa', 'Biólogo', 'U-PB-004'),
    (1005, 'Elisa Maria Nunes', 'Assistente de saúde pública', 'U-PB-005')
ON CONFLICT(id) DO NOTHING;

-- Amostra de agravos/doenças para testes. Não é uma lista completa.
INSERT INTO AgravoDoenca (cid10, nome) VALUES
      ('A90',   'Dengue'),
      ('A71',   'Tracoma'),
      ('B01.9', 'Varicela sem complicações'),
      ('B03',   'Varíola'),
      ('Y09',   'Violência interpessoal/autoprovocada'),
      ('X29',   'Acidente por animais peçonhentos'),
      ('A15',   'Tuberculose respiratória'),
      ('A30',   'Hanseníase'),
      ('B54',   'Malária não especificada'),
      ('B57',   'Doença de Chagas'),
      ('A82',   'Raiva'),
      ('A95',   'Febre amarela'),
      ('A92.0', 'Febre de Chikungunya'),
      ('A92.8', 'Zika vírus'),
      ('B15',   'Hepatite A'),
      ('B16',   'Hepatite B'),
      ('B18.2', 'Hepatite C viral crônica'),
      ('A00',   'Cólera'),
      ('A01',   'Febre tifóide e paratifóide'),
      ('A36',   'Difteria'),
      ('A37',   'Coqueluche'),
      ('A33',   'Tétano neonatal'),
      ('A35',   'Tétano acidental (outros)'),
      ('A39',   'Infecção meningocócica'),
      ('B05',   'Sarampo'),
      ('B06',   'Rubéola'),
      ('B24',   'Doença pelo vírus da imunodeficiência humana [HIV]'),
      ('A50',   'Sífilis congênita'),
      ('A53',   'Sífilis adquirida'),
      ('A87',   'Meningite viral'),
      ('G00',   'Meningite bacteriana não especificada'),
      ('A80',   'Poliomielite aguda'),
      ('A27',   'Leptospirose'),
      ('B08.1', 'Eritema infeccioso'),
      ('B26',   'Caxumba'),
      ('A81',   'Doença de Creutzfeldt-Jakob'),
      ('U07.1', 'COVID-19'),
      ('A77.0', 'Febre maculosa'),
      ('A08',   'Infecções intestinais virais'),
      ('B55',   'Leishmaniose'),
      ('A20',   'Peste'),
      ('A22',   'Carbúnculo'),
      ('B01',   'Varicela (Catapora)'),
      ('J10',   'Influenza devida a vírus influenza identificado'),
      ('W54',   'Mordedura ou golpe provocado por cão'),
      ('X27',   'Contato com escorpião'),
      ('X28',   'Contato com abelha, vespa ou marimbondo'),
      ('Z20.3', 'Contato com e exposição à raiva'),
      ('Z20.4', 'Contato com e exposição à rubéola'),
      ('Z21',   'Estado de infecção assintomática pelo HIV')
ON CONFLICT(cid10) DO NOTHING;