package edu.ifspb.jppgmx.smfin.paciente;

public enum RacaCor {
    BRANCO(1, "Branco(a)"), PRETO(2, "Preto(a)"),
    AMARELO(3, "Amarelo(a)"), PARDO(4, "Pardo(a)"),
    INDIGENA(5, "Indígena"), IGNORADO(9, "Ignorado");

    private final int codigo;
    private final String descricao;

    RacaCor(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
}
