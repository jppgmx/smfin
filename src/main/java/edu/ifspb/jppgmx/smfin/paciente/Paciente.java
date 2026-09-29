package edu.ifspb.jppgmx.smfin.paciente;

import edu.ifspb.jppgmx.smfin.localidade.EnderecoResidencial;

import java.time.LocalDate;

public record Paciente(
    int id,
    String nome,
    LocalDate dataNascimento,
    Integer idade,
    IdadeUnidade idadeUnidade,
    Sexo sexo,
    Gestante gestante,
    RacaCor racaCor,
    Escolaridade escolaridade,
    String cns,
    String nomeMae,
    EnderecoResidencial endereco
) {
}
