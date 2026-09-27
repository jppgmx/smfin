package edu.ifspb.jppgmx.smfin.paciente;

import edu.ifspb.jppgmx.smfin.DominioCodificado;

public enum RacaCor implements DominioCodificado<Integer> {
    BRANCO(1, "Branco(a)"), PRETO(2, "Preto(a)"),
    AMARELO(3, "Amarelo(a)"), PARDO(4, "Pardo(a)"),
    INDIGENA(5, "Indígena"), IGNORADO(9, "Ignorado");

    private final int codigo;
    private final String descricao;

    RacaCor(int codigo, String descricao) {
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
