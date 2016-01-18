package io.github.jonestimd.subset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class finds subsets of a collection that match some criteria.  The criteria are specified by an implementation
 * of {@link SubsetPredicate}.  The {@code static} factory methods can be used to create an instance based on the
 * category of the collection.
 *
 * <p><strong>Note:</strong> This class is not thread safe.
 */
public abstract class SubsetSearch<T> {
    private SubsetPredicate<T> criteria;
    private List<List<T>> matches = new ArrayList<>();
    private CombinationVisitor<T> accumulator = new CombinationVisitor<T>() {
        public boolean itemAdded(List<T> subset, T item) {
            SubsetPredicateResult result = criteria.apply(item);
            if (result == SubsetPredicateResult.MATCH) {
                matches.add(subset);
            }
            return ! isEndNode(result);
        }

        public void itemRemoved(T item) {
            criteria.remove(item);
        }
    };

    protected SubsetSearch(SubsetPredicate<T> criteria) {
        this.criteria = criteria;
    }

    /**
     * Perform the search for subsets matching the criteria.
     * @return the matching subsets
     */
    public List<List<T>> findSubSets(Collection<T> items) {
        return findSubSets(new ArrayList<>(items));
    }

    /**
     * Perform the search for subsets matching the criteria.
     * @return the matching subsets
     */
    public List<List<T>> findSubSets(List<T> items) {
        criteria.reset();
        matches.clear();
        Combinations.visitCombinations(items, accumulator);
        return matches;
    }

    /**
     * @return true if the result indicates that supersets of the current set will not match.
     */
    protected abstract boolean isEndNode(SubsetPredicateResult result);

    /**
     * Create a {@link SubsetSearch} for use on a collection having uniform sign (e.g. all positive or negative numbers with no zeros).
     * The returned instance will not consider supersets of a subset for which the predicate returns
     * {@link SubsetPredicateResult#MATCH} or {@link SubsetPredicateResult#TOO_MANY}.
     */
    public static <T> SubsetSearch<T> uniformSign(SubsetPredicate<T> criteria) {
        return new SubsetSearch<T>(criteria) {
            protected boolean isEndNode(SubsetPredicateResult result) {
                return result == SubsetPredicateResult.MATCH || result == SubsetPredicateResult.TOO_MANY;
            }
        };
    }

    /**
     * Create a {@link SubsetSearch} for use on a collection having mixed sign (e.g. a mix of positive numbers,
     * negative numbers and zeros). The returned instance will consider all possible subsets.
     */
    public static <T> SubsetSearch<T> mixedSign(SubsetPredicate<T> criteria) {
        return new SubsetSearch<T>(criteria) {
            protected boolean isEndNode(SubsetPredicateResult result) {
                return false;
            }
        };
    }
}