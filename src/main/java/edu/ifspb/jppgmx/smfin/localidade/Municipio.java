package edu.ifspb.jppgmx.smfin.localidade;

import tools.jackson.databind.JsonNode;

public record Municipio(int codigoIbge, String nome, Estado estado) {
    public static Municipio parse(JsonNode node, Estado estado) {
        return new Municipio(
            node.get("id").asInt(),
            node.get("nome").asString(),
            estado
        );
    }
}
