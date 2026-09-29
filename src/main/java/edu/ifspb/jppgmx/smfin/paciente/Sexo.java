package edu.ifspb.jppgmx.smfin.paciente;

import edu.ifspb.jppgmx.smfin.DominioCodificado;

public enum Sexo implements DominioCodificado<Character> {
    MASCULINO('M', "Masculino"), FEMININO('F', "Feminino"),
    IGNORADO('I', "Ignorado");

    private final char codigo;
    private final String descricao;

    Sexo(char codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public Character getCodigo() {
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
