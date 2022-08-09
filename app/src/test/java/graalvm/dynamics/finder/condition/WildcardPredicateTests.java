package graalvm.dynamics.finder.condition;

import graalvm.dynamics.finder.condition.predicate.WildcardPredicate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WildcardPredicateTests {
    @Test
    void matchAll() {
        WildcardPredicate predicate = new WildcardPredicate("*");
        assertThat(predicate).accepts("some", "test", "strings", "");
    }

    @Test
    void wildcardAtStart() {
        WildcardPredicate predicate = new WildcardPredicate("*test");
        assertThat(predicate).accepts("1test", "sometest", "test");
        assertThat(predicate).rejects("test1", "testsome", "te-st");
    }

    @Test
    void wildcardAtEnd() {
        WildcardPredicate predicate = new WildcardPredicate("test*");
        assertThat(predicate).accepts("test1", "testsome", "test");
        assertThat(predicate).rejects("1test", "sometest", "te-st");
    }

    @Test
    void wildcardInBetween() {
        WildcardPredicate predicate = new WildcardPredicate("some*test");
        assertThat(predicate).accepts("sometest", "some1test", "some-and-some-more-test");
        assertThat(predicate).rejects("sometest1", "");
    }
}
