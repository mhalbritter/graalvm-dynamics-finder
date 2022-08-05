package graalvm.dynamics.finder.model;

import com.fasterxml.jackson.databind.JsonNode;

public record CallStackElement(CallStackElement parent, String clazz, String method, String signature) {
    public String getCompleteStack() {
        StringBuilder stringBuilder = new StringBuilder();
        toStringAppend(stringBuilder);
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return this.clazz + "." + this.method + this.signature;
    }

    private void toStringAppend(StringBuilder stringBuilder) {
        stringBuilder.append(this.clazz).append('.').append(this.method).append(this.signature).append('\n');
        if (this.parent != null) {
            this.parent.toStringAppend(stringBuilder);
        }
    }

    public static CallStackElement parse(CallStackElement parent, JsonNode node) {
        return new CallStackElement(
            parent,
            node.get("class").textValue(),
            node.get("method").textValue(),
            node.get("signature").textValue()
        );
    }
}
