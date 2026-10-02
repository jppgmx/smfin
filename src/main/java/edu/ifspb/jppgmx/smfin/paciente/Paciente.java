package edu.ifspb.jppgmx.smfin.paciente;

import edu.ifspb.jppgmx.smfin.localidade.EnderecoResidencial;

import java.time.LocalDate;

public record Paciente(
    int id,
    String nome,
    String nomeMae,
    String numeroCns,
    LocalDate dataNascimento,
    Integer idade,
    IdadeUnidade idadeUnidade,
    Sexo sexo,
    Gestante gestante,
    RacaCor racaCor,
    Escolaridade escolaridade,
    EnderecoResidencial enderecoResidencial
) {
}
