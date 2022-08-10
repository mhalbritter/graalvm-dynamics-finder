package graalvm.dynamics.finder.reflection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graalvm.dynamics.finder.condition.Conditions;
import graalvm.dynamics.finder.model.CallStackElement;
import graalvm.dynamics.finder.model.DynamicConfig;
import graalvm.dynamics.finder.model.ReflectionConfig;
import graalvm.dynamics.finder.outputwriter.ReportWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Command(name = "reflection")
public class ReflectionCommand implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionCommand.class);

    @Option(names = {"-i", "--input"}, required = true, paramLabel = "INPUT-FILE", description = "Input file, usually named partial-config-with-origins.json. Required.")
    private Path inputFile = null;

    @Option(names = {"-o", "--output"}, required = true, paramLabel = "OUTPUT-FILE", description = "Output file. Supported extensions: '.md', '.adoc'. Required.")
    private Path outputFile = null;

    @Option(names = {"-t", "--target"}, required = true, split = ",", paramLabel = "TARGET", description = "Condition to match classes on which reflection has been used. Required.")
    private List<String> targets = null;

    @Option(names = {"-c", "--caller"}, required = false, split = ",", paramLabel = "CALLER", description = "Condition to match classes which are in a stack which leads to reflection. If not specified, all callers are included. Can be used multiple times.")
    private List<String> callers = List.of();

    @Option(names = {"--exclude-caller"}, required = false, split = ",", paramLabel = "EXCLUDED-CALLER", description = "Condition to match classes which must not be in a stack which leads to reflection. If not specified, no callers are excluded. Can be used multiple times.")
    private List<String> excludedCallers = List.of();

    @Option(names = {"--direct-caller"}, required = false, split = ",", paramLabel = "DIRECT-CALLER", description = "Condition to match classes which do reflection. If not specified, all direct callers are included. Can be used multiple times.")
    private List<String> directCallers = List.of();

    @Override
    public void run() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Running reflection sub-command");
            LOGGER.info("Input file: '{}'", this.inputFile.toAbsolutePath());
            LOGGER.info("Output file: '{}'", this.outputFile.toAbsolutePath());
            LOGGER.info("Targets: '{}'", this.targets);
            LOGGER.info("Direct callers: '{}'", this.directCallers);
            LOGGER.info("Callers: '{}'", this.callers);
            LOGGER.info("Excluded callers: '{}'", this.excludedCallers);
        }

        try {
            analyze();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        LOGGER.info("Done. See '{}' for the report", this.outputFile.toAbsolutePath());
    }

    private void analyze() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(this.inputFile.toFile()).get("configuration-with-origins");
        Conditions targets = Conditions.parseMultiple(this.targets);
        Conditions directCallers = Conditions.parseMultiple(this.directCallers);
        Conditions callers = Conditions.parseMultiple(this.callers);
        Conditions excludedCallers = Conditions.parseMultiple(this.excludedCallers);

        try (ReportWriter reportWriter = ReportWriter.forPath(outputFile)) {
            visitNode(root, targets, directCallers, callers, excludedCallers, CallStackElement.parse(null, root), reportWriter);
        }
    }

    private void visitNode(JsonNode node, Conditions targets, Conditions directCallers, Conditions callers, Conditions excludedCallers, CallStackElement callStackElement, ReportWriter reportWriter) throws IOException {
        LOGGER.trace("Visiting {}", callStackElement);

        DynamicConfig dynamicConfig = DynamicConfig.parse(node);

        for (ReflectionConfig reflectionConfig : dynamicConfig.reflectionConfig()) {
            LOGGER.debug("Found reflection config for '{}'", reflectionConfig.clazz());
            if (isTargetIncluded(targets, reflectionConfig.clazz()) && isDirectCallerIncluded(directCallers, callStackElement) && isCallerIncluded(callers, callStackElement) && !isCallerExcluded(excludedCallers, callStackElement)) {
                reportWriter.reportReflectionFinding(reflectionConfig, callStackElement);
            }
        }

        for (JsonNode methodNode : node.get("methods")) {
            CallStackElement nextCallStackElement = CallStackElement.parse(callStackElement, methodNode);
            visitNode(methodNode, targets, directCallers, callers, excludedCallers, nextCallStackElement, reportWriter);
        }
    }

    private static boolean isTargetIncluded(Conditions targets, String clazz) {
        return targets.isIncluded(clazz);
    }

    private boolean isDirectCallerIncluded(Conditions directCallers, CallStackElement callStackElement) {
        if (directCallers.isEmpty()) {
            // By default, all callers are included
            return true;
        }

        // The given callStackElement points to the class where the reflection is in, for example java.lang.Class.forName()
        // We need to find the next class in the callstack, which lead to the reflection call
        CallStackElement nextClass = callStackElement.findNextClass();
        if (nextClass == null) {
            return false;
        }

        return directCallers.isIncluded(nextClass.clazz());
    }

    private boolean isCallerExcluded(Conditions excludedCallers, CallStackElement callStackElement) {
        if (excludedCallers.isEmpty()) {
            // By default, no caller is excluded
            return false;
        }

        CallStackElement current = callStackElement;
        while (current != null) {
            if (excludedCallers.isIncluded(current.clazz())) {
                return true;
            }
            current = current.parent();
        }
        return false;
    }

    private boolean isCallerIncluded(Conditions callers, CallStackElement callStackElement) {
        if (callers.isEmpty()) {
            // By default, all callers are included
            return true;
        }

        CallStackElement current = callStackElement;
        while (current != null) {
            if (callers.isIncluded(current.clazz())) {
                return true;
            }
            current = current.parent();
        }
        return false;
    }
}
