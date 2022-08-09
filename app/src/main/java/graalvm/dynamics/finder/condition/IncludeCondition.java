package graalvm.dynamics.finder.condition;

import graalvm.dynamics.finder.condition.predicate.ConditionPredicate;

class IncludeCondition implements Condition {
    private final ConditionPredicate predicate;

    IncludeCondition(ConditionPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean isIncluded(String name, boolean included) {
        if (included) {
            // It's included anyway, so no need to include it further
            return true;
        }

        if (predicate.test(name)) {
            return true;
        }
        return included;
    }

    @Override
    public String toReportString() {
        return "+" + this.predicate.toReportString();
    }

    @Override
    public String toString() {
        return "IncludeCondition{" +
            "predicate=" + predicate +
            '}';
    }
}
