package edu.ifspb.jppgmx.smfin.notificacao;

import edu.ifspb.jppgmx.smfin.DominioCodificado;

public enum DoencaRelacionadoTrabalho implements DominioCodificado<Integer> {
    SIM(1, "Sim"), NAO(2, "Não"), IGNORADO(9, "Ignorado");

    private final int codigo;
    private final String descricao;

    DoencaRelacionadoTrabalho(int codigo, String descricao) {
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
