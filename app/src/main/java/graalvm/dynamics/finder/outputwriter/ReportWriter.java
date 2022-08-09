package graalvm.dynamics.finder.outputwriter;

import graalvm.dynamics.finder.model.CallStackElement;
import graalvm.dynamics.finder.model.ReflectionConfig;

import java.io.IOException;
import java.nio.file.Path;

public interface ReportWriter extends AutoCloseable {
    void reportReflectionFinding(ReflectionConfig reflectionConfig, CallStackElement callStackElement) throws IOException;

    static ReportWriter markdown(Path outputFile) throws IOException {
        return new MarkdownWriter(outputFile);
    }

    static ReportWriter asciiDoc(Path outputFile) throws IOException {
        return new AsciiDocWriter(outputFile);
    }

    static ReportWriter forPath(Path file) throws IOException {
        if (file.getFileName().toString().endsWith(".md")) {
            return ReportWriter.markdown(file);
        }
        if (file.getFileName().toString().endsWith(".adoc")) {
            return ReportWriter.asciiDoc(file);
        }
        throw new IllegalArgumentException("Unknown file extension of file '%s'. Supported: '.md' and '.adoc'".formatted(file.getFileName()));
    }
}
