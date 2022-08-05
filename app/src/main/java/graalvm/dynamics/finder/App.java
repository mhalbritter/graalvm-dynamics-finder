package graalvm.dynamics.finder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graalvm.dynamics.finder.model.CallStackElement;
import graalvm.dynamics.finder.model.DynamicConfig;
import graalvm.dynamics.finder.model.ReflectionConfig;
import graalvm.dynamics.finder.outputwriter.OutputWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

@Command(
    name = "graalvm-dynamics-finder",
    version = "0.0.1-SNAPSHOT",
    mixinStandardHelpOptions = true
)
public class App implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @Option(names = {"-i", "--input"}, required = true, paramLabel = "INPUT-FILE", description = "Input file, usually named partial-config-with-origins.json.")
    private Path inputFile;

    @Option(names = {"-o", "--output"}, required = true, paramLabel = "OUTPUT-FILE", description = "Output file. Supported extensions: '.md', '.adoc'.")
    private Path outputFile;

    @Option(names = {"-t", "--reflection-targets"}, required = true, paramLabel = "REFLECTION-TARGET", description = "Target classes to find reflection access for.", split = ",")
    private Set<String> reflectionTargets;

    @Override
    public void run() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Using input file '{}'", this.inputFile.toAbsolutePath());
            LOGGER.info("Using output file '{}'", this.outputFile.toAbsolutePath());
            LOGGER.info("Looking for classes '{}'", this.reflectionTargets);
        }

        try {
            analyze(this.inputFile, this.outputFile, this.reflectionTargets);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        LOGGER.info("Done. See '{}' for the report", this.outputFile.toAbsolutePath());
    }

    private void analyze(Path inputFile, Path outputFile, Set<String> reflectionTargets) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(inputFile.toFile()).get("configuration-with-origins");

        try (OutputWriter outputWriter = OutputWriter.forPath(outputFile)) {
            visitNode(root, reflectionTargets, CallStackElement.parse(null, root), outputWriter);
        }
    }

    private void visitNode(JsonNode node, Set<String> reflectionTargets, CallStackElement callStackElement, OutputWriter outputWriter) throws IOException {
        LOGGER.trace("Visiting {}", callStackElement);

        DynamicConfig dynamicConfig = DynamicConfig.parse(node);

        for (ReflectionConfig reflectionConfig : dynamicConfig.reflectionConfig()) {
            LOGGER.debug("Found reflection config for '{}'", reflectionConfig.clazz());
            if (reflectionTargets.contains(reflectionConfig.clazz())) {
                outputWriter.reportReflectionFinding(reflectionConfig, callStackElement);
            }
        }

        for (JsonNode methodNode : node.get("methods")) {
            CallStackElement nextCallStackElement = CallStackElement.parse(callStackElement, methodNode);
            visitNode(methodNode, reflectionTargets, nextCallStackElement, outputWriter);
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
