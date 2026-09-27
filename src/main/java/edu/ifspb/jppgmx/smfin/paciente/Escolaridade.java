package edu.ifspb.jppgmx.smfin.paciente;

public enum Escolaridade {
    ANALFABETO(0, "Analfabeto"),
    FUNDAMENTAL_I_INCOMPLETO(1, "1ª a 4ª série incompleta do EF (antigo primário ou 1º grau)"),
    FUNDAMENTAL_I_COMPLETO(2, "4ª série completa do EF (antigo primário ou 1º grau)"),
    FUNDAMENTAL_II_INCOMPLETO(3, "5ª à 8ª série incompleta do EF (antigo ginásio ou 1º grau)"),
    FUNDAMENTAL_COMPLETO(4, "Ensino fundamental completo (antigo ginásio ou 1º grau)"),
    MEDIO_INCOMPLETO(5, "Ensino médio incompleto (antigo colegial ou 2º grau)"),
    MEDIO_COMPLETO(6, "Ensino médio completo (antigo colegial ou 2º grau)"),
    SUPERIOR_INCOMPLETO(7, "Educação superior incompleta"),
    SUPERIOR_COMPLETO(8, "Educação superior completa"),
    IGNORADO(9, "Ignorado"),
    NAO_SE_APLICA(10, "Não se aplica");

    private final int codigo;
    private final String descricao;

    Escolaridade(int codigo, String descricao) {
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
