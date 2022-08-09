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

    @Option(names = {"-i", "--input"}, required = true, paramLabel = "INPUT-FILE", description = "Input file, usually named partial-config-with-origins.json.")
    private Path inputFile;

    @Option(names = {"-o", "--output"}, required = true, paramLabel = "OUTPUT-FILE", description = "Output file. Supported extensions: '.md', '.adoc'.")
    private Path outputFile;

    @Option(names = {"-t", "--target"}, required = true, split = ",", paramLabel = "TARGET", description = "Condition to match classes on which reflection has been used.")
    private List<String> targets;

    @Override
    public void run() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Running reflection sub-command");
            LOGGER.info("Input file: '{}'", this.inputFile.toAbsolutePath());
            LOGGER.info("Output file: '{}'", this.outputFile.toAbsolutePath());
            LOGGER.info("Targets: '{}'", this.targets);
        }

        try {
            analyze(this.inputFile, this.outputFile, this.targets);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        LOGGER.info("Done. See '{}' for the report", this.outputFile.toAbsolutePath());
    }

    private void analyze(Path inputFile, Path outputFile, List<String> reflectionTargets) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(inputFile.toFile()).get("configuration-with-origins");
        Conditions conditions = Conditions.parseMultiple(reflectionTargets);

        try (ReportWriter reportWriter = ReportWriter.forPath(outputFile)) {
            visitNode(root, conditions, CallStackElement.parse(null, root), reportWriter);
        }
    }

    private void visitNode(JsonNode node, Conditions targets, CallStackElement callStackElement, ReportWriter reportWriter) throws IOException {
        LOGGER.trace("Visiting {}", callStackElement);

        DynamicConfig dynamicConfig = DynamicConfig.parse(node);

        for (ReflectionConfig reflectionConfig : dynamicConfig.reflectionConfig()) {
            LOGGER.debug("Found reflection config for '{}'", reflectionConfig.clazz());
            if (targets.isIncluded(reflectionConfig.clazz())) {
                reportWriter.reportReflectionFinding(reflectionConfig, callStackElement);
            }
        }

        for (JsonNode methodNode : node.get("methods")) {
            CallStackElement nextCallStackElement = CallStackElement.parse(callStackElement, methodNode);
            visitNode(methodNode, targets, nextCallStackElement, reportWriter);
        }
    }
}
