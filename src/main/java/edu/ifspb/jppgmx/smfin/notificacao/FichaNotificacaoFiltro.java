package edu.ifspb.jppgmx.smfin.notificacao;

import java.time.LocalDate;

public record FichaNotificacaoFiltro(
        String q,
        Integer tipo,
        String agravo,
        String unidade,
        Integer municipio,
        LocalDate dataNotificacaoInicio,
        LocalDate dataNotificacaoFim,
        Boolean duplicata
) {
}
