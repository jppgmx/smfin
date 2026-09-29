package edu.ifspb.jppgmx.smfin.notificacao;

import edu.ifspb.jppgmx.smfin.localidade.EnderecoResidencial;
import edu.ifspb.jppgmx.smfin.localidade.Localidade;
import edu.ifspb.jppgmx.smfin.localidade.Municipio;
import edu.ifspb.jppgmx.smfin.paciente.Paciente;

import java.time.LocalDate;

public record FichaNotificacao(
    String id,
    TipoNotificacao tipo,
    AgravoDoenca agravoDoenca,
    LocalDate dataNotificacao,
    Municipio municipioNotificado,
    Unidade unidadeNotificadora,
    LocalDate dataSintomas,
    Paciente paciente,
    EnderecoResidencial endereco,
    LocalDate dataInvestigacao,
    ClassificacaoFinal classificacaoFinal,
    CriterioConfirmacao criterioConfirmacaoDescarte,
    Autoctone autoctone,
    Localidade localProvavelInfeccao,
    DoencaRelacionadaTrabalho relacionadoTrabalho,
    EvolucaoCaso evolucaoCaso,
    LocalDate dataObito,
    LocalDate dataEncerramento,
    String observacoes,
    Investigador investigadorResponsavel,
    String assinaturaResponsavel,
    LocalDate assinadoEm,
    LocalDate atualizadoEm
) {
}
