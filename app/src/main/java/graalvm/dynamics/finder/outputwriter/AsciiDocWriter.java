package graalvm.dynamics.finder.outputwriter;

import graalvm.dynamics.finder.model.CallStackElement;
import graalvm.dynamics.finder.model.ReflectionConfig;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class AsciiDocWriter implements OutputWriter {
    private final BufferedWriter fileOutputStream;

    AsciiDocWriter(Path outputFile) throws IOException {
        this.fileOutputStream = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    @Override
    public void reportReflectionFinding(ReflectionConfig reflectionConfig, CallStackElement callStackElement) throws IOException {
        // Heading
        this.fileOutputStream.write("== Reflection access on ");
        this.fileOutputStream.write(reflectionConfig.clazz());
        this.fileOutputStream.write("\n\n");

        // Config
        this.fileOutputStream.write("[source]\n");
        this.fileOutputStream.write("----\n");
        this.fileOutputStream.write(reflectionConfig.node().toPrettyString());
        this.fileOutputStream.write("\n----\n\n");

        // Callstack
        writeCallStack(callStackElement);
    }

    @Override
    public void close() throws Exception {
        this.fileOutputStream.close();
    }

    private void writeCallStack(CallStackElement callStackElement) throws IOException {
        this.fileOutputStream.write("[source]\n");
        this.fileOutputStream.write("----\n");
        this.fileOutputStream.write(callStackElement.getCompleteStack());
        this.fileOutputStream.write("\n----\n");
    }
}
