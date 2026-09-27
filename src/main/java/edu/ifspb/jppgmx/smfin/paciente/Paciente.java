package edu.ifspb.jppgmx.smfin.paciente;

import java.time.LocalDate;

public record Paciente(
    String nome,
    LocalDate dataNascimento,
    int idadeAparente,
    UnidadeIdade unidadeIdade,
    Sexo sexo,
    Gestante gestante,
    RacaCor racaCor,
    Escolaridade escolaridade,
    String cns,
    String nomeMae
) {
}
