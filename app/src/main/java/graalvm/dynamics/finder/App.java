package graalvm.dynamics.finder;

import graalvm.dynamics.finder.condition.DebugConditionsSubCommand;
import graalvm.dynamics.finder.reflection.ReflectionCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "graalvm-dynamics-finder",
    version = "0.0.1-SNAPSHOT",
    subcommands = {ReflectionCommand.class, DebugConditionsSubCommand.class},
    mixinStandardHelpOptions = true
)
class App {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
