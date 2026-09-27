package edu.ifspb.jppgmx.smfin.notificacao;

import edu.ifspb.jppgmx.smfin.localidade.Municipio;

public record Unidade(int codigo, String nome, Unidade.Tipo tipo, Municipio municipio) {
    public enum Tipo {
        UBS, HOSPITAL, UPA, CLINICA, OUTRO
    }
}
