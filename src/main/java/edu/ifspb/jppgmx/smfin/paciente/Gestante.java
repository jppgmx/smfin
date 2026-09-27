package edu.ifspb.jppgmx.smfin.paciente;

import edu.ifspb.jppgmx.smfin.DominioCodificado;

public enum Gestante implements DominioCodificado<Integer> {
    PRIMEIRO_TRIMESTRE(1, "1º Trimestre"),
    SEGUNDO_TRIMESTRE(2, "2º Trimestre"),
    TERCEIRO_TRIMESTRE(3, "3º Trimestre"),
    IDADE_GESTACIONAL_IGNORADA(4, "Idade Gestacional Ignorada"),
    NAO_GESTANTE(5, "Não Gestante"), NAO_SE_APLICA(6, "Não se aplica"),
    IGNORADO(9, "Ignorado");

    private final int codigo;
    private final String descricao;

    Gestante(int codigo, String descricao) {
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
