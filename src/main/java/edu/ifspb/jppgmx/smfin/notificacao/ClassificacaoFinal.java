package edu.ifspb.jppgmx.smfin.notificacao;

import edu.ifspb.jppgmx.smfin.DominioCodificado;

public enum ClassificacaoFinal implements DominioCodificado<Integer> {
    CONFIRMADO(1, "Confirmado"), DESCARTADO(2, "Descartado");

    private final int codigo;
    private final String descricao;

    ClassificacaoFinal(int codigo, String descricao) {
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
