package graalvm.dynamics.finder.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Conditions implements Predicate<String> {
    private final List<Condition> conditions;

    Conditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public boolean isIncluded(String name) {
        boolean included = false;
        for (Condition condition : conditions) {
            included = condition.isIncluded(name, included);
        }
        return included;
    }

    @Override
    public boolean test(String s) {
        return isIncluded(s);
    }

    TestWithDebugReport evaluateWithDebugReport(String s) {
        ConditionDebugReport debugReport = new ConditionDebugReport();
        debugReport.subject(s);

        boolean included = false;
        debugReport.startState(included);

        int index = 0;
        for (Condition condition : conditions) {
            debugReport.beforeCondition(condition, index, included);
            included = condition.isIncluded(s, included);
            debugReport.afterCondition(condition, index, included);
            index++;
        }
        debugReport.endState(included);
        return new TestWithDebugReport(included, debugReport);
    }

    public static Conditions parseCommaDelimited(String input) {
        String[] elements = input.split(",");
        List<Condition> conditions = new ArrayList<>(elements.length);
        for (String element : elements) {
            conditions.add(Condition.parse(element));
        }
        return new Conditions(conditions);
    }

    public static Conditions parseMultiple(Iterable<String> multiple) {
        List<Condition> conditions = new ArrayList<>();
        for (String condition : multiple) {
            conditions.add(Condition.parse(condition));
        }
        return new Conditions(conditions);
    }

    record TestWithDebugReport(boolean included, ConditionDebugReport report) {
    }
}
