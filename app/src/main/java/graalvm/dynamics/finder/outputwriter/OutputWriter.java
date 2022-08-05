package graalvm.dynamics.finder.outputwriter;

import graalvm.dynamics.finder.model.CallStackElement;
import graalvm.dynamics.finder.model.ReflectionConfig;

import java.io.IOException;
import java.nio.file.Path;

public interface OutputWriter extends AutoCloseable {
    void reportReflectionFinding(ReflectionConfig reflectionConfig, CallStackElement callStackElement) throws IOException;

    static OutputWriter markdown(Path outputFile) throws IOException {
        return new MarkdownWriter(outputFile);
    }

    static OutputWriter asciiDoc(Path outputFile) throws IOException {
        return new AsciiDocWriter(outputFile);
    }

    static OutputWriter forPath(Path file) throws IOException {
        if (file.getFileName().toString().endsWith(".md")) {
            return OutputWriter.markdown(file);
        }
        if (file.getFileName().toString().endsWith(".adoc")) {
            return OutputWriter.asciiDoc(file);
        }
        throw new IllegalArgumentException("Unknown file extension of file '%s'. Supported are: '.md' and '.adoc'".formatted(file.getFileName()));
    }
}
