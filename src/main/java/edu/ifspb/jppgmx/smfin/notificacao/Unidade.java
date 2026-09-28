package edu.ifspb.jppgmx.smfin.notificacao;

import edu.ifspb.jppgmx.smfin.DominioCodificado;
import edu.ifspb.jppgmx.smfin.localidade.Municipio;

public record Unidade(String codigo, String nome, Unidade.Tipo tipo, Municipio municipio) {
    public enum Tipo implements DominioCodificado<Integer> {
        UBS(1, "UBS"),
        HOSPITAL(2, "Hospital"),
        UPA(3, "UPA"),
        CLINICA(4, "Clínica"),
        OUTRO(5, "Outro");

        private final int codigo;
        private final String descricao;

        Tipo(int codigo, String descricao) {
            this.codigo = codigo;
            this.descricao = descricao;
        }

        @Override
        public Integer getCodigo() {
            return codigo;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }
}
