package io.github.jonestimd.subset;

import java.math.BigDecimal;
import java.util.function.Function;

import org.junit.Test;

import static io.github.jonestimd.subset.SubsetPredicateResult.*;
import static org.junit.Assert.*;

public class BigDecimalSubsetPredicateTest {
    private static Function<BigDecimal, BigDecimal> NOOP_ADAPTER = container -> container;

    @Test
    public void positiveGoalZeroError() throws Exception {
        BigDecimalSubsetPredicate<BigDecimal> predicate =
                new BigDecimalSubsetPredicate<>(NOOP_ADAPTER, BigDecimal.TEN);

        assertSame(TOO_FEW, predicate.apply(new BigDecimal(9)));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE));
        assertSame(TOO_MANY, predicate.apply(BigDecimal.ONE));

        predicate.remove(new BigDecimal(3));
        assertSame(TOO_FEW, predicate.apply(BigDecimal.ONE));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE));
        assertSame(TOO_MANY, predicate.apply(BigDecimal.ONE));

        predicate.reset();
        assertSame(TOO_FEW, predicate.apply(new BigDecimal(9)));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE));
        assertSame(TOO_MANY, predicate.apply(BigDecimal.ONE));
    }

    @Test
    public void positiveGoalWithError() throws Exception {
        BigDecimalSubsetPredicate<BigDecimal> predicate =
                new BigDecimalSubsetPredicate<>(NOOP_ADAPTER, BigDecimal.TEN, BigDecimal.ONE);

        assertSame(TOO_FEW, predicate.apply(new BigDecimal(8)));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE));
        assertSame(TOO_MANY, predicate.apply(BigDecimal.ONE));

        predicate.remove(new BigDecimal(5));
        assertSame(TOO_FEW, predicate.apply(BigDecimal.ONE));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE));
        assertSame(TOO_MANY, predicate.apply(BigDecimal.ONE));

        predicate.reset();
        assertSame(TOO_FEW, predicate.apply(new BigDecimal(8)));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE));
        assertSame(TOO_MANY, predicate.apply(BigDecimal.ONE));
    }

    @Test
    public void negativeGoalZeroError() throws Exception {
        BigDecimalSubsetPredicate<BigDecimal> predicate =
                new BigDecimalSubsetPredicate<>(NOOP_ADAPTER, BigDecimal.TEN.negate());

        assertSame(TOO_FEW, predicate.apply(new BigDecimal(-9)));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(TOO_MANY, predicate.apply(BigDecimal.ONE.negate()));

        predicate.remove(new BigDecimal(-3));
        assertSame(TOO_FEW, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(TOO_MANY, predicate.apply(BigDecimal.ONE.negate()));

        predicate.reset();
        assertSame(TOO_FEW, predicate.apply(new BigDecimal(-9)));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(TOO_MANY, predicate.apply(BigDecimal.ONE.negate()));
    }

    @Test
    public void negativeGoalWithError() throws Exception {
        BigDecimalSubsetPredicate<BigDecimal> predicate =
                new BigDecimalSubsetPredicate<>(NOOP_ADAPTER, BigDecimal.TEN.negate(), BigDecimal.ONE);

        assertSame(TOO_FEW, predicate.apply(new BigDecimal(-8)));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(TOO_MANY, predicate.apply(BigDecimal.ONE.negate()));

        predicate.remove(new BigDecimal(-5));
        assertSame(TOO_FEW, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(TOO_MANY, predicate.apply(BigDecimal.ONE.negate()));

        predicate.reset();
        assertSame(TOO_FEW, predicate.apply(new BigDecimal(-8)));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(MATCH, predicate.apply(BigDecimal.ONE.negate()));
        assertSame(TOO_MANY, predicate.apply(BigDecimal.ONE.negate()));
    }
}
