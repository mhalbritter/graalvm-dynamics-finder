package graalvm.dynamics.finder.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Set;

public record ResourceConfig(JsonNode node, Set<String> includePatterns, Set<String> bundles) {
    public static final ResourceConfig EMPTY = new ResourceConfig(null, Set.of(), Set.of());

    public boolean isEmpty() {
        return this.includePatterns.isEmpty() && this.bundles.isEmpty();
    }

    public static ResourceConfig parse(JsonNode node) {
        // TODO: Implement parsing
        // See https://www.graalvm.org/22.2/reference-manual/native-image/dynamic-features/Resources/
        // "{"resources":{"includes":[{"pattern":"\\QMETA-INF/spring.factories\\E"}]},"bundles":[]}"
        return new ResourceConfig(node, Set.of(), Set.of());
    }
}
