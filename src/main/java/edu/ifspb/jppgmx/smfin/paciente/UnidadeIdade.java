package edu.ifspb.jppgmx.smfin.paciente;

public enum UnidadeIdade {
    HORAS(1, "Hora", "Horas"), DIAS(2, "Dia", "Dias"),
    MESES(3, "Mês", "Meses"), ANOS(4, "Ano", "Anos");

    private final int codigo;
    private final String singular;
    private final String plural;

    UnidadeIdade(int codigo, String singular, String plural) {
        this.codigo = codigo;
        this.singular = singular;
        this.plural = plural;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getSingular() {
        return singular;
    }

    public String getPlural() {
        return plural;
    }

    public String formatar(int valor) {
        return String.format(
            "%d - %s", valor,
            valor > 1 ? plural : singular
        );
    }
}
