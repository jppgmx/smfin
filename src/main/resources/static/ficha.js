const form = document.querySelector("#notification-form");
const message = document.querySelector("#form-message");
const mode = form.dataset.mode;
const fichaId = form.dataset.id;
let original = null;
let municipios = [];
let estados = [];
let unidades = [];
let agravos = [];

const enumEndpoints = {
    gestante: "/api/enums/gestantes",
    racaCor: "/api/enums/racas-cor",
    escolaridade: "/api/enums/escolaridades",
    zona: "/api/enums/zonas",
    classificacaoFinal: "/api/enums/classificacoes-finais",
    criterio: "/api/enums/criterios-confirmacao",
    autoctone: "/api/enums/autoctones",
    trabalho: "/api/enums/doencas-relacionadas-trabalho",
    evolucao: "/api/enums/evolucoes-caso"
};

const enumNames = {
    tipo: { 1: "NEGATIVA", 2: "INDIVIDUAL", 3: "SURTO", 4: "TRACOMA" },
    idadeUnidade: { 1: "HORAS", 2: "DIAS", 3: "MESES", 4: "ANOS" },
    sexo: { M: "MASCULINO", F: "FEMININO", I: "IGNORADO" },
    gestante: { 1: "PRIMEIRO_TRIMESTRE", 2: "SEGUNDO_TRIMESTRE", 3: "TERCEIRO_TRIMESTRE", 4: "IDADE_GESTACIONAL_IGNORADA", 5: "NAO_GESTANTE", 6: "NAO_SE_APLICA", 9: "IGNORADO" },
    racaCor: { 1: "BRANCO", 2: "PRETO", 3: "AMARELO", 4: "PARDO", 5: "INDIGENA", 9: "IGNORADO" },
    escolaridade: { 0: "ANALFABETO", 1: "FUNDAMENTAL_I_INCOMPLETO", 2: "FUNDAMENTAL_I_COMPLETO", 3: "FUNDAMENTAL_II_INCOMPLETO", 4: "FUNDAMENTAL_COMPLETO", 5: "MEDIO_INCOMPLETO", 6: "MEDIO_COMPLETO", 7: "SUPERIOR_INCOMPLETO", 8: "SUPERIOR_COMPLETO", 9: "IGNORADO", 10: "NAO_SE_APLICA" },
    zona: { 1: "URBANA", 2: "RURAL", 3: "PERIURBANA", 9: "IGNORADO" },
    classificacaoFinal: { 1: "CONFIRMADO", 2: "DESCARTADO" },
    criterio: { 1: "LABORATORIAL", 2: "CLINICO_EPIDEMIOLOGICO" },
    autoctone: { 1: "SIM", 2: "NAO", 3: "INDETERMINADO" },
    trabalho: { 1: "SIM", 2: "NAO", 9: "IGNORADO" },
    evolucao: { 1: "CURA", 2: "OBITO_PELO_AGRAVO", 3: "OBITO_POR_OUTRAS_CAUSAS", 9: "IGNORADO" }
};

function code(value) {
    return value && typeof value === "object" ? value.codigo : value;
}

function text(value) {
    return value && typeof value === "object" ? value.descricao : value;
}

function enumText(name) {
    const select = form.elements[name];
    return select?.selectedOptions[0]?.textContent || null;
}

function setValue(name, value) {
    const field = form.elements[name];
    if (field) field.value = value ?? "";
}

function fillSelect(name, values) {
    const select = form.elements[name];
    if (!select) return;
    const first = select.options[0];
    select.replaceChildren(first);
    values.forEach((value) => {
        const option = document.createElement("option");
        option.value = value.codigo;
        option.textContent = `${value.codigo} - ${value.descricao}`;
        select.append(option);
    });
}

async function getJson(url) {
    const response = await fetch(url);
    if (!response.ok) throw new Error(`Não foi possível consultar ${url}`);
    return response.json();
}

async function loadOptions() {
    const [agravoList, estadoList, municipioList, ...enumLists] = await Promise.all([
        getJson("/api/agravo-doenca"),
        getJson("/api/ibge/estados"),
        getJson("/api/ibge/estados/PB/municipios"),
        ...Object.values(enumEndpoints).map(getJson)
    ]);
    agravos = agravoList;
    estados = estadoList;
    municipios = municipioList;

    const agravoSelect = form.elements.agravo;
    agravoList.forEach((value) => agravoSelect.append(new Option(`${value.cid10} - ${value.nome}`, value.cid10)));
    estados.forEach((value) => form.elements.ufNotificacao.append(new Option(`${value.sigla} - ${value.nome}`, value.sigla)));
    ["municipio", "municipioResidencia", "municipioInfeccao"].forEach((name) => {
        municipios.forEach((value) => form.elements[name].append(new Option(`${value.codigoIbge} - ${value.nome}`, value.codigoIbge)));
    });
    Object.keys(enumEndpoints).forEach((name, index) => fillSelect(name, enumLists[index]));
}

async function loadUnits(municipio) {
    const unitSelect = form.elements.unidade;
    const investigatorSelect = form.elements.investigador;
    unidades = [];
    unitSelect.replaceChildren(new Option("Carregando...", ""));
    investigatorSelect.replaceChildren(new Option("Selecione a unidade primeiro", ""));
    if (!municipio) {
        unitSelect.replaceChildren(new Option("Selecione o município primeiro", ""));
        return;
    }
    unidades = await getJson(`/api/unidades/por-municipio?municipio=${encodeURIComponent(municipio)}`);
    unitSelect.replaceChildren(new Option("Selecione", ""));
    unidades.forEach((value) => unitSelect.append(new Option(`${value.codigo} - ${value.nome}`, value.codigo)));
}

async function loadInvestigators(unitCode) {
    const select = form.elements.investigador;
    select.replaceChildren(new Option("Carregando...", ""));
    if (!unitCode) {
        select.replaceChildren(new Option("Selecione a unidade primeiro", ""));
        return;
    }
    const investigators = await getJson(`/api/unidades/${encodeURIComponent(unitCode)}/investigadores`);
    select.replaceChildren(new Option("Nenhum", ""));
    investigators.forEach((value) => select.append(new Option(`${value.nome} - ${value.funcao}`, value.id)));
}

async function loadNotificationMunicipios(uf) {
    const select = form.elements.municipio;
    select.replaceChildren(new Option("Carregando...", ""));
    municipios = uf ? await getJson(`/api/ibge/estados/${encodeURIComponent(uf)}/municipios`) : [];
    select.replaceChildren(new Option("Selecione", ""));
    municipios.forEach((value) => {
        select.append(new Option(`${value.codigoIbge} - ${value.nome}`, value.codigoIbge));
    });
}

function selected(list, value) {
    return list.find((item) => String(item.codigo) === String(value)) ?? null;
}

function municipality(value, source = municipios) {
    const result = source.find((item) => String(item.codigoIbge) === String(value));
    return result ? { ...result, estado: result.estado } : null;
}

function patientFromForm() {
    const individual = form.elements.tipo.value === "2";
    if (!individual) return null;
    const residence = municipality(form.elements.municipioResidencia.value);
    const localidade = {
        id: 0,
        pais: null,
        municipio: residence,
        distrito: form.elements.distrito.value || null,
        bairro: form.elements.bairro.value || null
    };
    return {
        id: original?.paciente?.id ?? 0,
        nome: form.elements.pacienteNome.value,
        nomeMae: form.elements.nomeMae.value || null,
        numeroCns: form.elements.numeroCns.value || null,
        dataNascimento: form.elements.dataNascimento.value || null,
        idade: form.elements.idade.value ? Number(form.elements.idade.value) : null,
        idadeUnidade: form.elements.idadeUnidade.value ? enumText("idadeUnidade") : null,
        sexo: form.elements.sexo.value ? enumText("sexo") : null,
        gestante: form.elements.gestante.value ? enumText("gestante") : null,
        racaCor: form.elements.racaCor.value ? enumText("racaCor") : null,
        escolaridade: form.elements.escolaridade.value ? enumText("escolaridade") : null,
        enderecoResidencial: {
            id: original?.paciente?.enderecoResidencial?.id ?? 0,
            base: localidade,
            logradouro: form.elements.logradouro.value || null,
            codigoLogradouro: null,
            numero: form.elements.numero.value || null,
            complemento: form.elements.complemento.value || null,
            geocampo1: null,
            geocampo2: null,
            pontoReferencia: null,
            cep: form.elements.cep.value || null,
            telefone: form.elements.telefone.value || null,
            zona: form.elements.zona.value ? enumText("zona") : null
        }
    };
}

function payload() {
    const notificationMunicipio = municipality(form.elements.municipio.value);
    const infectionMunicipio = municipality(form.elements.municipioInfeccao.value);
    const unidade = unidades.find((item) => item.codigo === form.elements.unidade.value);
    const investigatorId = form.elements.investigador.value;
    const investigator = original?.investigadorResponsavel?.id === Number(investigatorId)
        ? original.investigadorResponsavel
        : investigatorId ? {
            id: Number(investigatorId),
            nome: form.elements.investigador.selectedOptions[0]?.textContent ?? "",
            funcao: "",
            unidade
        } : null;
    return {
        id: fichaId || null,
        tipo: enumText("tipo"),
        agravoDoenca: agravos.find((item) => item.cid10 === form.elements.agravo.value),
        dataNotificacao: form.elements.dataNotificacao.value,
        municipioNotificado: notificationMunicipio,
        unidadeNotificadora: unidade,
        dataSintoma: form.elements.dataSintoma.value || null,
        paciente: patientFromForm(),
        dataInvestigacao: form.elements.dataInvestigacao.value,
        classificacaoFinal: form.elements.classificacaoFinal.value ? enumText("classificacaoFinal") : null,
        criterioConfirmacaoDescarte: form.elements.criterio.value ? enumText("criterio") : null,
        autoctone: form.elements.autoctone.value ? enumText("autoctone") : null,
        localProvavelInfeccao: infectionMunicipio ? { id: 0, pais: form.elements.paisInfeccao.value || null, municipio: infectionMunicipio, distrito: form.elements.distritoInfeccao.value || null, bairro: form.elements.bairroInfeccao.value || null } : null,
        relacionadoTrabalho: form.elements.trabalho.value ? enumText("trabalho") : null,
        evolucaoCaso: form.elements.evolucao.value ? enumText("evolucao") : null,
        dataObito: form.elements.dataObito.value || null,
        dataEncerramento: form.elements.dataEncerramento.value || null,
        observacoes: form.elements.observacoes.value || null,
        investigadorResponsavel: investigator
    };
}

function populate(ficha) {
    original = ficha;
    setValue("tipo", code(ficha.tipo));
    setValue("agravo", ficha.agravoDoenca?.cid10);
    setValue("dataNotificacao", ficha.dataNotificacao);
    setValue("municipio", ficha.municipioNotificado?.codigoIbge);
    setValue("unidade", ficha.unidadeNotificadora?.codigo);
    setValue("investigador", ficha.investigadorResponsavel?.id);
    setValue("dataSintoma", ficha.dataSintoma);
    setValue("dataInvestigacao", ficha.dataInvestigacao);
    setValue("classificacaoFinal", code(ficha.classificacaoFinal));
    setValue("criterio", code(ficha.criterioConfirmacaoDescarte));
    setValue("autoctone", code(ficha.autoctone));
    setValue("trabalho", code(ficha.relacionadoTrabalho));
    setValue("evolucao", code(ficha.evolucaoCaso));
    setValue("dataObito", ficha.dataObito);
    setValue("dataEncerramento", ficha.dataEncerramento);
    setValue("observacoes", ficha.observacoes);
    setValue("paisInfeccao", ficha.localProvavelInfeccao?.pais);
    setValue("municipioInfeccao", ficha.localProvavelInfeccao?.municipio?.codigoIbge);
    setValue("distritoInfeccao", ficha.localProvavelInfeccao?.distrito);
    setValue("bairroInfeccao", ficha.localProvavelInfeccao?.bairro);
    if (!ficha.paciente) return;
    const patient = ficha.paciente;
    const address = patient.enderecoResidencial;
    setValue("pacienteNome", patient.nome);
    setValue("nomeMae", patient.nomeMae);
    setValue("numeroCns", patient.numeroCns);
    setValue("dataNascimento", patient.dataNascimento);
    setValue("idade", patient.idade);
    setValue("idadeUnidade", code(patient.idadeUnidade));
    setValue("sexo", code(patient.sexo));
    setValue("gestante", code(patient.gestante));
    setValue("racaCor", code(patient.racaCor));
    setValue("escolaridade", code(patient.escolaridade));
    setValue("municipioResidencia", address?.base?.municipio?.codigoIbge);
    setValue("distrito", address?.base?.distrito);
    setValue("bairro", address?.base?.bairro);
    setValue("logradouro", address?.logradouro);
    setValue("numero", address?.numero);
    setValue("complemento", address?.complemento);
    setValue("cep", address?.cep);
    setValue("telefone", address?.telefone);
    setValue("zona", code(address?.zona));
}

async function start() {
    try {
        await loadOptions();
        if (mode === "editar") {
            const ficha = await getJson(`/api/notificacao/${encodeURIComponent(fichaId)}`);
            const uf = ficha.municipioNotificado?.estado?.sigla;
            setValue("ufNotificacao", uf);
            await loadNotificationMunicipios(uf);
            await loadUnits(ficha.municipioNotificado?.codigoIbge);
            await loadInvestigators(ficha.unidadeNotificadora?.codigo);
            populate(ficha);
        }
        lockImmutableFields();
        updateSections();
    } catch (error) {
        showMessage(error.message, true);
    }
}

function lockImmutableFields() {
    if (mode !== "editar") return;
    const editable = new Set([
        "dataSintoma", "dataInvestigacao", "classificacaoFinal", "criterio",
        "autoctone", "trabalho", "evolucao", "dataObito", "dataEncerramento",
        "observacoes"
    ]);
    for (const field of form.elements) {
        if (field.name && !editable.has(field.name)) field.disabled = true;
    }
}

function updateSections() {
    const individual = form.elements.tipo.value === "2";
    document.querySelector("#individual-section").hidden = !individual;
    document.querySelector("#residence-section").hidden = !individual;
    document.querySelector("#infection-section").hidden = form.elements.classificacaoFinal.value !== "1";
}

function showMessage(value, error = false) {
    message.hidden = false;
    message.className = `form-message ${error ? "error" : "success"}`;
    message.textContent = value;
}

form.elements.tipo.addEventListener("change", updateSections);
form.elements.classificacaoFinal.addEventListener("change", updateSections);
form.elements.ufNotificacao.addEventListener("change", async () => {
    try {
        await loadNotificationMunicipios(form.elements.ufNotificacao.value);
        await loadUnits("");
    } catch (error) {
        showMessage(error.message, true);
    }
});
form.elements.municipio.addEventListener("change", async () => {
    try {
        await loadUnits(form.elements.municipio.value);
    } catch (error) {
        showMessage(error.message, true);
    }
});
form.elements.unidade.addEventListener("change", async () => {
    try {
        await loadInvestigators(form.elements.unidade.value);
    } catch (error) {
        showMessage(error.message, true);
    }
});
form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const method = mode === "editar" ? "PUT" : "POST";
    const url = mode === "editar" ? `/api/notificacao/${encodeURIComponent(fichaId)}` : "/api/notificacao";
    const response = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload())
    });
    if (!response.ok) {
        const detail = await response.text();
        showMessage(detail || "Não foi possível salvar a ficha.", true);
        return;
    }
    window.location.href = "/";
});

start();
