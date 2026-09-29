package edu.ifspb.jppgmx.smfin;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

public interface DominioCodificado<C> {
    C getCodigo();
    String getDescricao();
    default String formatarDominio() {
        return String.format("%s - %s", getCodigo().toString(), getDescricao());
    }
    default JsonNode toJsonNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("codigo", getCodigo().toString());
        node.put("descricao", getDescricao());
        return node;
    }

    static <C, D extends DominioCodificado<C>> D fromCodigo(C codigo, Class<? extends D> enumClass) {
        if (!enumClass.isEnum() && !DominioCodificado.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException("A classe fornecida não é um enum que implementa DominioCodificado.");
        }

        D[] values = enumClass.getEnumConstants();
        for (D value : values) {
            if (value.getCodigo().equals(codigo)) {
                return value;
            }
        }
        return null;
    }
}
