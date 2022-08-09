package graalvm.dynamics.finder.condition;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;

@Command(name = "debug-conditions")
public class DebugConditionsSubCommand implements Runnable {
    @Option(names = {"-c", "--conditions"}, required = true, split = ",", paramLabel = "CONDITIONS", description = "Conditions to test")
    private List<String> conditions;

    @Option(names = {"-t", "--test-subject"}, required = true, paramLabel = "TEST-SUBJECT", description = "Subject to test the conditions on")
    private String subject;

    @Override
    public void run() {
        Conditions conditions = Conditions.parseMultiple(this.conditions);
        Conditions.TestWithDebugReport report = conditions.evaluateWithDebugReport(this.subject);
        if (report.included()) {
            System.out.printf("'%s' is included%n", this.subject);
        } else {
            System.out.printf("'%s' is NOT included%n", this.subject);
        }
        System.out.println();
        System.out.println("=== DEBUG REPORT ===");
        System.out.println(report.report().getReport());
    }
}
