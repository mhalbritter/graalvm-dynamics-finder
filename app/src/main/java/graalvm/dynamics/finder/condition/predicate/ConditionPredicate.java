package graalvm.dynamics.finder.condition.predicate;

import java.util.function.Predicate;

public interface ConditionPredicate extends Predicate<String> {
    String toReportString();

    static ConditionPredicate wildcard(String pattern) {
        return new WildcardPredicate(pattern);
    }
}
