package graalvm.dynamics.finder.condition;

import graalvm.dynamics.finder.condition.predicate.ConditionPredicate;

class ExcludeCondition implements Condition {
    private final ConditionPredicate predicate;

    ExcludeCondition(ConditionPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean isIncluded(String name, boolean included) {
        if (!included) {
            // It's not included anyway, so no need to exclude it further
            return false;
        }

        if (predicate.test(name)) {
            return false;
        }
        return included;
    }

    @Override
    public String toReportString() {
        return "-" + this.predicate.toReportString();
    }

    @Override
    public String toString() {
        return "ExcludeCondition{" +
            "predicate=" + predicate +
            '}';
    }
}
