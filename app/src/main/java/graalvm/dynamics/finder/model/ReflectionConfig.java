package graalvm.dynamics.finder.model;

import com.fasterxml.jackson.databind.JsonNode;

public record ReflectionConfig(JsonNode node, String clazz) {
    public static ReflectionConfig parse(JsonNode node) {
        return new ReflectionConfig(node, node.get("name").textValue());
    }
}
