@param String nome

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Fichas de notificação - ${nome}</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body>
<main>
    <header class="page-header">
        <div>
            <h1>Fichas de notificação</h1>
            <p>${nome}</p>
        </div>
        <a class="button" href="#">Nova ficha</a>
    </header>

    <section class="filters" aria-label="Filtros">
        <form id="filters-form">
            <label>
                Busca
                <input type="search" name="q" placeholder="ID, paciente, agravo ou unidade">
            </label>
            <label>
                Tipo
                <select name="tipo">
                    <option value="">Todos</option>
                    <option value="1">Negativa</option>
                    <option value="2">Individual</option>
                    <option value="3">Surto</option>
                    <option value="4">Tracoma</option>
                </select>
            </label>
            <label>
                Agravo/doença
                <input type="text" name="agravo" placeholder="CID-10 ou nome">
            </label>
            <label>
                Unidade
                <input type="text" name="unidade" placeholder="Código ou nome">
            </label>
            <label>
                Data inicial
                <input type="date" name="dataNotificacaoInicio">
            </label>
            <label>
                Data final
                <input type="date" name="dataNotificacaoFim">
            </label>
            <label class="checkbox">
                <input type="checkbox" name="duplicata">
                Somente duplicadas
            </label>
            <div class="filter-actions">
                <button type="submit">Filtrar</button>
                <a href="/">Limpar</a>
            </div>
        </form>
    </section>

    <div class="table-header">
        <strong id="results-count">Carregando fichas...</strong>
        <label>
            Itens por página
            <select id="page-size" name="size">
                <option value="10">10</option>
                <option value="25">25</option>
                <option value="50">50</option>
            </select>
        </label>
    </div>

    <div class="table-container">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Tipo</th>
                <th>Agravo/doença</th>
                <th>Paciente</th>
                <th>Data da notificação</th>
                <th>Município</th>
                <th>Unidade</th>
                <th>Situação</th>
                <th aria-label="Ações"></th>
            </tr>
            </thead>
            <tbody id="notifications-body">
            <tr><td colspan="9">Carregando...</td></tr>
            </tbody>
        </table>
    </div>

    <nav class="pagination" aria-label="Paginação">
        <span id="pagination-summary">Página 1</span>
        <span id="pagination-links">
            <a href="#" data-page="previous" aria-label="Página anterior">Anterior</a>
            <a class="current" href="#" data-page="current">1</a>
            <a href="#" data-page="next" aria-label="Próxima página">Próxima</a>
        </span>
    </nav>
</main>
<script src="/app.js"></script>
</body>
</html>
