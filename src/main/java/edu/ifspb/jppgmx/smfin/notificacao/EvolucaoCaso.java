package edu.ifspb.jppgmx.smfin.notificacao;

import edu.ifspb.jppgmx.smfin.DominioCodificado;

public enum EvolucaoCaso implements DominioCodificado<Integer> {
    CURA(1, "Cura"),
    OBITO_PELO_AGRAVO(2, "Óbito pelo agravo notificado"),
    OBITO_POR_OUTRAS_CAUSAS(3, "Óbito por outras causas"),
    IGNORADO(9, "Ignorado");

    private final int codigo;
    private final String descricao;

    EvolucaoCaso(int codigo, String descricao) {
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
