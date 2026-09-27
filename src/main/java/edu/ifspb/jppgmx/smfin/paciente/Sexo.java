package edu.ifspb.jppgmx.smfin.paciente;

public enum Sexo {
    MASCULINO('M', "Masculino"), FEMININO('F', "Feminino"),
    IGNORADO('I', "Ignorado");

    private char sexo;
    private String displayName;

    Sexo(char sexo, String displayName) {
        this.sexo = sexo;
        this.displayName = displayName;
    }

    public char getSexo() {
        return sexo;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String toString() {
        return String.format("%c - %s", this.sexo, this.displayName);
    }
}
