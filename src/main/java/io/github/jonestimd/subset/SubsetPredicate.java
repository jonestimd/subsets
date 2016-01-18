package io.github.jonestimd.subset;

/**
 * This interface is used by {@link SubsetSearch} to find subsets of a collection that meet some criteria.  Implementations
 * only need to maintain enough state to determine if the current subset matches the criteria.  For example, given
 * a collection of numbers, to find subsets having a specific sum, the predicate would only need an accumulator for
 * the sum of the current subset.  The {@link #apply(Object)} and {@link #remove(Object)} operations must be
 * complementary, so beware of rounding errors if using floating point math.
 *
 * @param <T> the type of the collection elements
 */
public interface SubsetPredicate<T> {
    /**
     * Add an item to the current subset.
     * @return the match status of the new subset
     */
    SubsetPredicateResult apply(T item);

    /**
     * Remove an item from the current subset.
     */
    void remove(T item);

    /**
     * Reset the state of this predicate.
     */
    void reset();
}