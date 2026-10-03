package edu.ifspb.jppgmx.smfin;

import tools.jackson.databind.JsonNode;

public final class JsonUtils {
    private JsonUtils() {
    }

    public static boolean isValidObject(JsonNode node) {
        return node != null && node.isObject();
    }

    public static boolean isValidArray(JsonNode node) {
        return node != null && node.isArray();
    }


}
