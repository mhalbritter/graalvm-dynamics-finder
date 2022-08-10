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

    /**
     * Returns the next {@link CallStackElement} which differs in {@link #clazz()}.
     *
     * @return {@link CallStackElement} which differs in {@link #clazz()} or null, if no such element exists.
     */
    public CallStackElement findNextClass() {
        CallStackElement current = parent();
        while (current != null) {
            if (!current.clazz().equals(clazz())) {
                break;
            }
            current = current.parent();
        }
        return current;
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
