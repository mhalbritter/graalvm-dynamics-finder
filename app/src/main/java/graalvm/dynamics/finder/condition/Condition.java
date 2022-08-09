package graalvm.dynamics.finder.condition;

import graalvm.dynamics.finder.condition.predicate.ConditionPredicate;

interface Condition {
    boolean isIncluded(String name, boolean included);

    /**
     * Parses a condition string. Starting the input with a '-' will result in a {@link ExcludeCondition},
     * starting it with '+' or with any other character will result in a {@link IncludeCondition}.
     *
     * @param input input string to parse
     * @return condition
     */
    static Condition parse(String input) {
        String trimmed = input.trim();

        if (trimmed.startsWith("-")) {
            return new ExcludeCondition(ConditionPredicate.wildcard(trimmed.substring(1)));
        }
        if (trimmed.startsWith("+")) {
            return new IncludeCondition(ConditionPredicate.wildcard(trimmed.substring(1)));
        }
        return new IncludeCondition(ConditionPredicate.wildcard(trimmed));
    }

    String toReportString();
}
