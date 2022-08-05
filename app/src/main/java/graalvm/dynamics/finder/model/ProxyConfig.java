package graalvm.dynamics.finder.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.Set;

public record ProxyConfig(JsonNode node, Set<String> interfaces) {
    public static ProxyConfig parse(JsonNode node) {
        JsonNode interfacesNode = node.get("interfaces");
        Set<String> interfaces = new HashSet<>(interfacesNode.size());
        for (JsonNode iface : interfacesNode) {
            interfaces.add(iface.textValue());
        }
        return new ProxyConfig(node, interfaces);
    }
}
