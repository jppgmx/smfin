package edu.ifspb.jppgmx.smfin.notificacao;

public enum TipoNotificacao {
    NEGATIVA(1, "Negativa"), INDIVIDUAL(2, "Individual"),
    SURTO(3, "Surto"), TRACOMA(4, "Tracoma");

    private final int codigo;
    private final String descricao;

    TipoNotificacao(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return this.codigo;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public String toString() {
        return String.format("%d - %s", codigo, descricao);
    }
}
