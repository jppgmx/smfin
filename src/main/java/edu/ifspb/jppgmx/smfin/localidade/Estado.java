package edu.ifspb.jppgmx.smfin.localidade;

import tools.jackson.databind.JsonNode;

public record Estado(int id, String sigla, String nome) {
    public static Estado parse(JsonNode node) {
        return new Estado(
            node.get("id").asInt(),
            node.get("sigla").asString(),
            node.get("nome").asString()
        );
    }
}