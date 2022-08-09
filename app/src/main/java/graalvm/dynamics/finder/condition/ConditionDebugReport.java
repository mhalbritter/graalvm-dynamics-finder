package graalvm.dynamics.finder.condition;

class ConditionDebugReport {
    private final StringBuilder report = new StringBuilder();
    private boolean included;

    void subject(String subject) {
        this.report.append("Test subject: '%s'%n%n".formatted(subject));
    }

    void startState(boolean included) {
        this.report.append("Start state: %b%n%n".formatted(included));
        this.included = included;
    }

    void beforeCondition(Condition condition, int index, boolean included) {
        this.report.append("Included before %s at index %d: %b%n".formatted(condition.toReportString(), index, included));
        this.included = included;
    }

    void afterCondition(Condition condition, int index, boolean included) {
        boolean changed = this.included != included;
        if (changed) {
            this.report.append("Included after %s at index %d: %b [State change]%n%n".formatted(condition.toReportString(), index, included));
        } else {
            this.report.append("Included after %s at index %d: %b%n%n".formatted(condition.toReportString(), index, included));
        }
        this.included = included;
    }

    void endState(boolean included) {
        this.report.append("End state: %b%n".formatted(included));
        this.included = included;
    }

    String getReport() {
        return this.report.toString();
    }
}
