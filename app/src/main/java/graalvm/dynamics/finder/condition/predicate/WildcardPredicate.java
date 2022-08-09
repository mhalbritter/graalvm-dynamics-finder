package graalvm.dynamics.finder.condition.predicate;

import java.util.regex.Pattern;

class WildcardPredicate implements ConditionPredicate {
    private final Pattern regex;
    private final String exactMatch;
    private final String pattern;

    WildcardPredicate(String pattern) {
        this.pattern = pattern;
        if (pattern.contains("*")) {
            this.regex = transformToRegex(pattern);
            this.exactMatch = null;
        } else {
            this.regex = null;
            this.exactMatch = pattern;
        }
    }

    @Override
    public boolean test(String s) {
        if (this.exactMatch != null) {
            return this.exactMatch.equals(s);
        }
        return this.regex.matcher(s).matches();
    }

    @Override
    public String toReportString() {
        return this.pattern;
    }

    @Override
    public String toString() {
        return "WildcardPredicate{" +
            "regex=" + regex +
            ", exactMatch='" + exactMatch + '\'' +
            ", pattern='" + pattern + '\'' +
            '}';
    }

    private static Pattern transformToRegex(String pattern) {
        StringBuilder regex = new StringBuilder();
        regex.append("^\\Q");
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '*') {
                regex.append("\\E");
                regex.append(".*?");
                regex.append("\\Q");
            } else {
                regex.append(c);
            }
        }
        regex.append("\\E$");
        return Pattern.compile(regex.toString());
    }
}
