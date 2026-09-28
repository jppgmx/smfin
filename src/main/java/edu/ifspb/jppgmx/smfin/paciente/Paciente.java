package edu.ifspb.jppgmx.smfin.paciente;

import java.time.LocalDate;

public record Paciente(
    String nome,
    LocalDate dataNascimento,
    int idadeAparente,
    IdadeUnidade idadeUnidade,
    Sexo sexo,
    Gestante gestante,
    RacaCor racaCor,
    Escolaridade escolaridade,
    String cns,
    String nomeMae
) {
}
