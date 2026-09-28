package edu.ifspb.jppgmx.smfin.localidade;

public record Localidade(
    int id,
    String pais,
    Municipio municipio,
    String distrito,
    String bairro
) {
    public Estado estado() {
        return municipio == null ? null : municipio.estado();
    }
}
