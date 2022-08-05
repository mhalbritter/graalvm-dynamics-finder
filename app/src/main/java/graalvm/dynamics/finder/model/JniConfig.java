package graalvm.dynamics.finder.model;

import com.fasterxml.jackson.databind.JsonNode;

public record JniConfig(JsonNode node, String clazz) {
    public static JniConfig parse(JsonNode node) {
        return new JniConfig(node, node.get("name").textValue());
    }
}
