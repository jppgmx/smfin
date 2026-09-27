package edu.ifspb.jppgmx.smfin.localidade;

import edu.ifspb.jppgmx.smfin.DominioCodificado;

public enum Zona implements DominioCodificado<Integer> {
    URBANA(1, "Urbana"), RURAL(2, "Rural"),
    PERIURBANA(3, "Periurbana"), IGNORADO(9, "Ignorado");

    private final int codigo;
    private final String descricao;

    Zona(int codigo, String descricao) {
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
