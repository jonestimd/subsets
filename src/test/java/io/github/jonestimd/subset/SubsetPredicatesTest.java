package io.github.jonestimd.subset;

import org.junit.Test;

import static io.github.jonestimd.subset.SubsetPredicateResult.*;
import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

public class SubsetPredicatesTest {
    @Test @SuppressWarnings("unchecked")
    public void applyCallsAllPredicatesForAnd() throws Exception {
        SubsetPredicate<String> predicate1 = mock(SubsetPredicate.class);
        SubsetPredicate<String> predicate2 = mock(SubsetPredicate.class);
        when(predicate1.apply("A")).thenReturn(MATCH);
        when(predicate2.apply("A")).thenReturn(MATCH);
        when(predicate1.apply("B")).thenReturn(TOO_FEW);
        when(predicate2.apply("B")).thenReturn(MATCH);
        when(predicate1.apply("C")).thenReturn(TOO_FEW);
        when(predicate2.apply("C")).thenReturn(TOO_MANY);
        SubsetPredicate<String> predicate = SubsetPredicates.and(predicate1, predicate2);

        assertThat(predicate.apply("A")).isSameAs(MATCH);
        assertThat(predicate.apply("B")).isSameAs(NO_MATCH);
        assertThat(predicate.apply("C")).isSameAs(TOO_MANY);

        verify(predicate1).apply("A");
        verify(predicate2).apply("A");
        verify(predicate1).apply("B");
        verify(predicate2).apply("B");
        verify(predicate1).apply("C");
        verify(predicate2).apply("C");
        verifyNoMoreInteractions(predicate1, predicate2);
    }

    @Test @SuppressWarnings("unchecked")
    public void removeCallsAllPredicatesForAnd() throws Exception {
        SubsetPredicate<String> predicate1 = mock(SubsetPredicate.class);
        SubsetPredicate<String> predicate2 = mock(SubsetPredicate.class);
        SubsetPredicate<String> predicate = SubsetPredicates.and(predicate1, predicate2);

        predicate.remove("A");

        verify(predicate1).remove("A");
        verify(predicate2).remove("A");
        verifyNoMoreInteractions(predicate1, predicate2);
    }

    @Test @SuppressWarnings("unchecked")
    public void resetCallsAllPredicatesForAnd() throws Exception {
        SubsetPredicate<String> predicate1 = mock(SubsetPredicate.class);
        SubsetPredicate<String> predicate2 = mock(SubsetPredicate.class);
        SubsetPredicate<String> predicate = SubsetPredicates.and(predicate1, predicate2);

        predicate.reset();

        verify(predicate1).reset();
        verify(predicate2).reset();
        verifyNoMoreInteractions(predicate1, predicate2);
    }
}
