package graalvm.dynamics.finder.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public record DynamicConfig(
    List<ReflectionConfig> reflectionConfig,
    List<ProxyConfig> proxyConfig,
    ResourceConfig resourceConfig,
    List<JniConfig> jniConfig
) {
    private static final DynamicConfig EMPTY = new DynamicConfig(List.of(), List.of(), ResourceConfig.EMPTY, List.of());

    public boolean isEmpty() {
        return this.reflectionConfig.isEmpty() && this.proxyConfig.isEmpty() && this.resourceConfig.isEmpty() && this.jniConfig.isEmpty();
    }

    public static DynamicConfig parse(JsonNode node) {
        if (!node.has("config")) {
            return EMPTY;
        }

        JsonNode configNode = node.required("config");

        // TODO: Implement support for serialization
        return new DynamicConfig(getReflectionConfig(configNode), getProxyConfig(configNode), getResourceConfig(configNode), getJniConfig(configNode));
    }

    private static List<JniConfig> getJniConfig(JsonNode configNode) {
        if (!configNode.has("jni")) {
            return List.of();
        }

        JsonNode jniNode = configNode.required("jni");

        List<JniConfig> result = new ArrayList<>(jniNode.size());
        for (JsonNode jniConfig : jniNode) {
            result.add(JniConfig.parse(jniConfig));
        }
        return result;
    }

    private static ResourceConfig getResourceConfig(JsonNode configNode) {
        if (!configNode.has("resource")) {
            return ResourceConfig.EMPTY;
        }

        JsonNode resourceNode = configNode.required("resource");
        return ResourceConfig.parse(resourceNode);
    }

    private static List<ProxyConfig> getProxyConfig(JsonNode configNode) {
        if (!configNode.has("proxy")) {
            return List.of();
        }

        JsonNode proxyNode = configNode.required("proxy");

        List<ProxyConfig> result = new ArrayList<>(proxyNode.size());
        for (JsonNode proxyConfig : proxyNode) {
            result.add(ProxyConfig.parse(proxyConfig));
        }
        return result;
    }

    private static List<ReflectionConfig> getReflectionConfig(JsonNode configNode) {
        if (!configNode.has("reflect")) {
            return List.of();
        }

        JsonNode reflectNode = configNode.required("reflect");

        List<ReflectionConfig> result = new ArrayList<>(reflectNode.size());
        for (JsonNode reflectConfig : reflectNode) {
            result.add(ReflectionConfig.parse(reflectConfig));
        }
        return result;
    }
}
