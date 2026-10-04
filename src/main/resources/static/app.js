const state = { page: 0 };

const form = document.querySelector("#filters-form");
const body = document.querySelector("#notifications-body");
const resultsCount = document.querySelector("#results-count");
const pageSize = document.querySelector("#page-size");
const paginationSummary = document.querySelector("#pagination-summary");
const paginationLinks = document.querySelector("#pagination-links");

function valueOf(object, property) {
    if (object == null) return "";
    if (typeof object === "string" || typeof object === "number") return object;
    return object[property] ?? "";
}

function formatDate(value) {
    if (!value) return "—";
    const [year, month, day] = value.split("-");
    return `${day}/${month}/${year}`;
}

function typeName(tipo) {
    const code = String(valueOf(tipo, "codigo"));
    return { 1: "Negativa", 2: "Individual", 3: "Surto", 4: "Tracoma" }[code] ?? valueOf(tipo, "descricao") ?? "—";
}

function notificationStatus(notification) {
    if (notification.dataEncerramento) {
        return '<span class="status status-done">Encerrada</span>';
    }
    return '<span class="status status-open">Em investigação</span>';
}

function cell(value) {
    const element = document.createElement("td");
    element.textContent = value || "—";
    return element;
}

function renderRows(notifications) {
    body.replaceChildren();
    if (!notifications.length) {
        const row = document.createElement("tr");
        const empty = document.createElement("td");
        empty.colSpan = 9;
        empty.textContent = "Nenhuma ficha encontrada.";
        row.append(empty);
        body.append(row);
        return;
    }

    notifications.forEach((notification) => {
        const row = document.createElement("tr");
        const agravo = valueOf(notification.agravoDoenca, "nome");
        const cid10 = valueOf(notification.agravoDoenca, "cid10");
        const paciente = valueOf(notification.paciente, "nome");
        const municipio = valueOf(notification.municipioNotificado, "nome");
        const unidade = valueOf(notification.unidadeNotificadora, "nome");

        row.append(
            cell(notification.id),
            cell(typeName(notification.tipo)),
            cell(cid10 && agravo ? `${cid10} - ${agravo}` : cid10 || agravo),
            cell(paciente),
            cell(formatDate(notification.dataNotificacao)),
            cell(municipio),
            cell(unidade)
        );

        const status = document.createElement("td");
        status.innerHTML = notificationStatus(notification);
        row.append(status);

        const actions = document.createElement("td");
        actions.className = "actions";
        const edit = document.createElement("a");
        edit.href = `/notificacao/${encodeURIComponent(notification.id)}/editar`;
        edit.textContent = "Editar";
        edit.title = "A API ainda não possui uma tela HTML de edição";
        const remove = document.createElement("a");
        remove.href = "#";
        remove.textContent = "Excluir";
        remove.addEventListener("click", (event) => removeNotification(event, notification.id));
        actions.append(edit, remove);
        row.append(actions);
        body.append(row);
    });
}

function requestParameters() {
    const parameters = new URLSearchParams(new FormData(form));
    parameters.set("page", state.page);
    parameters.set("size", pageSize.value);
    parameters.set("sort", "id,asc");
    if (form.elements.duplicata.checked) {
        parameters.set("duplicata", "true");
    } else {
        parameters.delete("duplicata");
    }
    for (const [key, value] of parameters) {
        if (!value) parameters.delete(key);
    }
    return parameters;
}

async function loadNotifications() {
    body.innerHTML = '<tr><td colspan="9">Carregando...</td></tr>';
    try {
        const response = await fetch(`/api/notificacao?${requestParameters()}`);
        if (!response.ok) throw new Error(`Erro HTTP ${response.status}`);
        const page = await response.json();
        renderRows(page.content);
        resultsCount.textContent = `${page.totalElements} ficha(s) encontrada(s)`;
        paginationSummary.textContent = `Página ${page.number + 1} de ${Math.max(page.totalPages, 1)}`;
        paginationLinks.querySelector("[data-page=current]").textContent = page.number + 1;
        paginationLinks.querySelector("[data-page=previous]").classList.toggle("disabled", page.first);
        paginationLinks.querySelector("[data-page=next]").classList.toggle("disabled", page.last);
    } catch (error) {
        body.innerHTML = '<tr><td colspan="9">Não foi possível carregar as fichas.</td></tr>';
        resultsCount.textContent = "Erro ao consultar fichas";
        console.error(error);
    }
}

async function removeNotification(event, id) {
    event.preventDefault();
    if (!window.confirm(`Excluir a ficha ${id}?`)) return;
    const response = await fetch(`/api/notificacao/${encodeURIComponent(id)}`, { method: "DELETE" });
    if (!response.ok) {
        window.alert("Não foi possível excluir a ficha.");
        return;
    }
    await loadNotifications();
}

form.addEventListener("submit", (event) => {
    event.preventDefault();
    state.page = 0;
    loadNotifications();
});

pageSize.addEventListener("change", () => {
    state.page = 0;
    loadNotifications();
});

paginationLinks.addEventListener("click", (event) => {
    const link = event.target.closest("a");
    if (!link || link.classList.contains("disabled")) return;
    event.preventDefault();
    if (link.dataset.page === "previous" && state.page > 0) state.page--;
    if (link.dataset.page === "next") state.page++;
    loadNotifications();
});

document.querySelector(".filter-actions a").addEventListener("click", (event) => {
    event.preventDefault();
    form.reset();
    state.page = 0;
    loadNotifications();
});

loadNotifications();
