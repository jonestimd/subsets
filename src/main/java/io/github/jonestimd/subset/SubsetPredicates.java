package io.github.jonestimd.subset;

import java.util.Arrays;
import java.util.List;

/**
 * Utilities for creating {@link SubsetPredicate}s.
 */
public class SubsetPredicates {
    private SubsetPredicates() {}

    /**
     * Create a predicate composed of the {@code ANDed} result of other predicates.
     * @param predicates the predicates to {@code AND}
     */
    @SafeVarargs
    public static <T> SubsetPredicate<T> and(SubsetPredicate<? super T>... predicates) {
        return new AndSubsetPredicate<>(Arrays.asList(predicates));
    }

    private static class AndSubsetPredicate<T> implements SubsetPredicate<T> {
        private List<SubsetPredicate<? super T>> predicates;

        private AndSubsetPredicate(List<SubsetPredicate<? super T>> predicates) {
            this.predicates = predicates;
        }

        public void remove(T item) {
            for (SubsetPredicate<? super T> predicate : predicates) {
                predicate.remove(item);
            }
        }

        public void reset() {
            predicates.forEach(SubsetPredicate::reset);
        }

        public SubsetPredicateResult apply(T input) {
            SubsetPredicateResult result = predicates.get(0).apply(input);
            for (int i = 1; i < predicates.size(); i++) {
                result = result.and(predicates.get(i).apply(input));
            }
            return result;
        }
    }
}