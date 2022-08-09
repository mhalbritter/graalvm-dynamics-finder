package graalvm.dynamics.finder.condition;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionsTests {
    @Test
    void includeAll() {
        Conditions conditions = Conditions.parseCommaDelimited("*");
        assertThat(conditions).accepts("some", "test", "strings", "");
    }

    @Test
    void excludeAll() {
        Conditions conditions = Conditions.parseCommaDelimited("-*");
        assertThat(conditions).rejects("some", "test", "strings", "");
    }

    @Test
    void includeSome() {
        Conditions conditions = Conditions.parseCommaDelimited("+foo,-bar");
        assertThat(conditions).accepts("foo");
        assertThat(conditions).rejects("bar", "baz", "");
    }

    @Test
    void emptyRejectsAll() {
        Conditions conditions = Conditions.parseCommaDelimited("");
        assertThat(conditions).rejects("foo", "bar", "baz");
    }
}
