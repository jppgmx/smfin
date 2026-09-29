package edu.ifspb.jppgmx.smfin.notificacao;

import edu.ifspb.jppgmx.smfin.DominioCodificado;

public enum CriterioConfirmacao implements DominioCodificado<Integer> {
    LABORATORIAL(1, "Laboratorial"),
    CLINICO_EPIDEMIOLOGICO(2, "Clínico-epidemiológico");

    private final int codigo;
    private final String descricao;

    CriterioConfirmacao(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return formatarDominio();
    }
}
