package edu.ifspb.jppgmx.smfin.notificacao;

import edu.ifspb.jppgmx.smfin.DominioCodificado;

public enum TipoNotificacao implements DominioCodificado<Integer> {
    NEGATIVA(1, "Negativa"), INDIVIDUAL(2, "Individual"),
    SURTO(3, "Surto"), TRACOMA(4, "Tracoma");

    private final int codigo;
    private final String descricao;

    TipoNotificacao(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public Integer getCodigo() {
        return this.codigo;
    }

    @Override
    public String getDescricao() {
        return this.descricao;
    }

    @Override
    public String toString() {
        return formatarDominio();
    }
}
