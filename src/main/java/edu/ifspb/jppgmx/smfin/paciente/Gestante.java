package edu.ifspb.jppgmx.smfin.paciente;

public enum Gestante {
    PRIMEIRO_TRIMESTRE(1, "1º Trimestre"),
    SEGUNDO_TRIMESTRE(2, "2º Trimestre"),
    TERCEIRO_TRIMESTRE(3, "3º Trimestre"),
    IDADE_GESTACIONAL_IGNORADA(4, "Idade Gestacional Ignorada"),
    NAO_GESTANTE(5, "Não Gestante"), NAO_SE_APLICA(6, "Não se aplica"),
    IGNORADO(9, "Ignorado");

    private int codigo;
    private final String descricao;

    Gestante(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return String.format("%d - %s", codigo, descricao);
    }
}
