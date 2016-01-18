package io.github.jonestimd.subset;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * An implementation of {@link SubsetPredicate} for {@link BigDecimal} values.
 * @param <T> the type of the collection items
 */
public class BigDecimalSubsetPredicate<T> implements SubsetPredicate<T> {
    private Function<T,BigDecimal> adapter;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal goalMinusError;
    private BigDecimal goalPlusError;
    private SubsetPredicateResult lowResult;
    private SubsetPredicateResult highResult;

    /**
     * @param adapter a function to extract {@link BigDecimal} values from the collection items
     * @param goal the target sum for the subsets
     */
    public BigDecimalSubsetPredicate(Function<T, BigDecimal> adapter, BigDecimal goal) {
        this(adapter, goal, BigDecimal.ZERO);
    }

    /**
     * @param adapter a function to extract {@link BigDecimal} values from the collection items
     * @param goal the target sum for the subsets
     * @param error margin of error for matching the target subset sum
     */
    public BigDecimalSubsetPredicate(Function<T, BigDecimal> adapter, BigDecimal goal, BigDecimal error) {
        this.adapter = adapter;
        this.goalMinusError = goal.subtract(error.abs());
        this.goalPlusError = goal.add(error.abs());
        this.lowResult = goal.signum() >= 0 ? SubsetPredicateResult.TOO_FEW : SubsetPredicateResult.TOO_MANY;
        this.highResult = goal.signum() >= 0 ? SubsetPredicateResult.TOO_MANY: SubsetPredicateResult.TOO_FEW;
    }

    public SubsetPredicateResult apply(T item) {
        total = adapter.apply(item).add(total);
        if (total.compareTo(goalMinusError) < 0) {
            return lowResult;
        }
        return total.compareTo(goalPlusError) > 0 ? highResult : SubsetPredicateResult.MATCH;
    }

    public void remove(T item) {
        total = total.subtract(adapter.apply(item));
    }

    public void reset() {
        total = BigDecimal.ZERO;
    }

    public String toString() {
        return "Total: " + total.toString() + " Goal: " + goalMinusError.toString() + " - " + goalPlusError.toString();
    }
}