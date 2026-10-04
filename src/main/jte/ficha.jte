@param String modo
@param String fichaId

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${modo.equals("editar") ? "Editar" : "Nova"} ficha de notificação</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body>
<main>
    <header class="page-header">
        <div>
            <h1>${modo.equals("editar") ? "Editar" : "Nova"} ficha de notificação</h1>
            <p>Preencha os campos conforme a ficha SINAN.</p>
        </div>
        <a href="/" class="button button-secondary">Voltar</a>
    </header>

    <div id="form-message" class="form-message" hidden></div>
    <form id="notification-form" data-mode="${modo}" data-id="${fichaId}">
        <fieldset>
            <legend>Cabeçalho e dados gerais</legend>
            <div class="form-grid">
                <label>Tipo de notificação *
                    <select name="tipo" required>
                        <option value="">Selecione</option>
                        <option value="1">1 - Negativa</option>
                        <option value="2">2 - Individual</option>
                        <option value="3">3 - Surto</option>
                        <option value="4">4 - Tracoma</option>
                    </select>
                </label>
                <label>Agravo/doença *
                    <select name="agravo" required>
                        <option value="">Carregando...</option>
                    </select>
                </label>
                <label>Data da notificação *
                    <input type="date" name="dataNotificacao" required>
                </label>
                <label>UF da notificação *
                    <select name="ufNotificacao" required>
                        <option value="">Carregando...</option>
                    </select>
                </label>
                <label>Município de notificação *
                    <select name="municipio" required>
                        <option value="">Carregando...</option>
                    </select>
                </label>
                <label>Unidade notificadora *
                    <select name="unidade" required>
                        <option value="">Selecione o município primeiro</option>
                    </select>
                </label>
                <label>Investigador responsável
                    <select name="investigador">
                        <option value="">Selecione a unidade primeiro</option>
                    </select>
                </label>
            </div>
        </fieldset>

        <fieldset id="individual-section">
            <legend>Notificação individual</legend>
            <div class="form-grid">
                <label>Data dos primeiros sintomas
                    <input type="date" name="dataSintoma">
                </label>
                <label>Nome do paciente
                    <input type="text" name="pacienteNome" maxlength="200">
                </label>
                <label>Nome da mãe
                    <input type="text" name="nomeMae" maxlength="200">
                </label>
                <label>Data de nascimento
                    <input type="date" name="dataNascimento">
                </label>
                <label>Idade
                    <input type="number" name="idade" min="0">
                </label>
                <label>Unidade da idade
                    <select name="idadeUnidade">
                        <option value="">Selecione</option>
                        <option value="1">1 - Hora</option>
                        <option value="2">2 - Dia</option>
                        <option value="3">3 - Mês</option>
                        <option value="4">4 - Ano</option>
                    </select>
                </label>
                <label>Sexo
                    <select name="sexo">
                        <option value="">Selecione</option>
                        <option value="M">M - Masculino</option>
                        <option value="F">F - Feminino</option>
                        <option value="I">I - Ignorado</option>
                    </select>
                </label>
                <label>Gestante
                    <select name="gestante"><option value="">Selecione</option></select>
                </label>
                <label>Raça/cor
                    <select name="racaCor"><option value="">Selecione</option></select>
                </label>
                <label>Escolaridade
                    <select name="escolaridade"><option value="">Selecione</option></select>
                </label>
                <label>Número do Cartão SUS
                    <input type="text" name="numeroCns" maxlength="15">
                </label>
            </div>
        </fieldset>

        <fieldset id="residence-section">
            <legend>Dados de residência</legend>
            <div class="form-grid">
                <label>Município de residência *
                    <select name="municipioResidencia">
                        <option value="">Selecione</option>
                    </select>
                </label>
                <label>Distrito
                    <input type="text" name="distrito">
                </label>
                <label>Bairro
                    <input type="text" name="bairro">
                </label>
                <label>Logradouro
                    <input type="text" name="logradouro">
                </label>
                <label>Número
                    <input type="text" name="numero">
                </label>
                <label>Complemento
                    <input type="text" name="complemento">
                </label>
                <label>CEP
                    <input type="text" name="cep">
                </label>
                <label>Telefone
                    <input type="text" name="telefone">
                </label>
                <label>Zona
                    <select name="zona"><option value="">Selecione</option></select>
                </label>
            </div>
        </fieldset>

        <fieldset>
            <legend>Investigação e conclusão</legend>
            <div class="form-grid">
                <label>Data da investigação *
                    <input type="date" name="dataInvestigacao" required>
                </label>
                <label>Classificação final
                    <select name="classificacaoFinal"><option value="">Selecione</option></select>
                </label>
                <label>Critério de confirmação/descarte
                    <select name="criterio"><option value="">Selecione</option></select>
                </label>
                <label>Autóctone
                    <select name="autoctone"><option value="">Selecione</option></select>
                </label>
                <label>Doença relacionada ao trabalho
                    <select name="trabalho"><option value="">Selecione</option></select>
                </label>
                <label>Evolução do caso
                    <select name="evolucao"><option value="">Selecione</option></select>
                </label>
                <label>Data do óbito
                    <input type="date" name="dataObito">
                </label>
                <label>Data de encerramento
                    <input type="date" name="dataEncerramento">
                </label>
            </div>
        </fieldset>

        <fieldset id="infection-section">
            <legend>Local provável da fonte de infecção</legend>
            <div class="form-grid">
                <label>País
                    <input type="text" name="paisInfeccao">
                </label>
                <label>Município
                    <select name="municipioInfeccao"><option value="">Selecione</option></select>
                </label>
                <label>Distrito
                    <input type="text" name="distritoInfeccao">
                </label>
                <label>Bairro
                    <input type="text" name="bairroInfeccao">
                </label>
            </div>
        </fieldset>

        <fieldset>
            <legend>Observações</legend>
            <label>Observações adicionais
                <textarea name="observacoes" rows="5"></textarea>
            </label>
        </fieldset>

        <div class="form-actions">
            <a href="/" class="button button-secondary">Cancelar</a>
            <button type="submit">Salvar ficha</button>
        </div>
    </form>
</main>
<script src="/ficha.js"></script>
</body>
</html>
