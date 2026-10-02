package edu.ifspb.jppgmx.smfin.notificacao;

import tools.jackson.databind.JsonNode;

public record AgravoDoenca(String cid10, String nome) {
    public static AgravoDoenca parse(JsonNode node) {
        return new AgravoDoenca(
                node.get("cid10").asString(),
                node.get("nome").asString()
        );
    }
}
