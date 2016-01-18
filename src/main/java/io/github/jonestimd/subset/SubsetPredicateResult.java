package io.github.jonestimd.subset;

/**
 * Status values for a {@link SubsetPredicate}.
 */
public enum SubsetPredicateResult {
    /**
     * The subset has exceeded the goal.
     * Should be used when it is certain that additional elements will move farther from the goal
     * (e.g. the collection elements always have the same sign).
     */
    TOO_MANY(0, 0, 0, 0),
    /**
     * The subset has not reached the goal value.
     * Should be used when it is certain that additional elements will move toward the goal.
     * (e.g. the collection elements always have the same sign).
     */
    TOO_FEW(0, 1, 2, 2),
    /**
     * The subset does not match the goal.
     * Should be used when additional elements could move farther from or nearer to the goal.
     * (e.g. the collection elements may be positive or negative).
     */
    NO_MATCH(0, 2, 2, 2),
    /**
     * The subset matches the goal.
     */
    MATCH(0, 2, 2, 3);

    private final int[] andResults;

    SubsetPredicateResult(int... andResults) {
        this.andResults = andResults;
    }

    /**
     * @return the logical combination of this result and another result for the same collection.
     */
    public SubsetPredicateResult and(SubsetPredicateResult other) {
        return values()[andResults[other.ordinal()]];
    }
}