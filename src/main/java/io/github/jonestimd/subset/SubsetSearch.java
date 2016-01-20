package io.github.jonestimd.subset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class finds subsets of a collection that match some criteria.  The criteria are specified by an implementation
 * of {@link SubsetPredicate}.  The {@code static} factory methods can be used to create an instance of this class based
 * on the category (uniform sign or mixed sign) of the collection.
 *
 * <p>The subsets are searched by adding and removing items to a working subset and notifying the predicate.  When an
 * item is added to the working subset,the predicate returns a {@link SubsetPredicateResult} indicating if the current
 * subset matches the criteria.  When an item is removed from the working subset, the predicate is notified so that
 * it can update its state.  For subsets that do not meet the criteria, the predicate can return one of 3 values,
 * to indicate how the search should proceed.  The first 2 values should be used for uniform sign collections and
 * the last value should be used for mixed sign collections.
 * <ul>
 *     <li>{@link SubsetPredicateResult#TOO_FEW} - items should be added to the subset to move toward the goal</li>
 *     <li>{@link SubsetPredicateResult#TOO_MANY} - items should be removed from the subset to move toward the goal</li>
 *     <li>{@link SubsetPredicateResult#NO_MATCH} - it is unknown if items should be added or removed</li>
 * </ul>
 *
 * <p><strong>Note:</strong> This class is not thread safe and each instance should only be accessed by a single thread.
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