package io.github.jonestimd.subset;

import org.junit.Test;

import static io.github.jonestimd.subset.SubsetPredicateResult.*;
import static org.junit.Assert.*;

public class SubsetPredicateResultTest {
    @Test
    public void tooManyAnd() throws Exception {
        assertSame(TOO_MANY, TOO_MANY.and(TOO_MANY));
        assertSame(TOO_MANY, TOO_MANY.and(TOO_FEW));
        assertSame(TOO_MANY, TOO_MANY.and(NO_MATCH));
        assertSame(TOO_MANY, TOO_MANY.and(MATCH));
    }

    @Test
    public void tooFewAnd() throws Exception {
        assertSame(TOO_MANY, TOO_FEW.and(TOO_MANY));
        assertSame(TOO_FEW, TOO_FEW.and(TOO_FEW));
        assertSame(NO_MATCH, TOO_FEW.and(NO_MATCH));
        assertSame(NO_MATCH, TOO_FEW.and(MATCH));
    }

    @Test
    public void noMatchAnd() throws Exception {
        assertSame(TOO_MANY, NO_MATCH.and(TOO_MANY));
        assertSame(NO_MATCH, NO_MATCH.and(TOO_FEW));
        assertSame(NO_MATCH, NO_MATCH.and(NO_MATCH));
        assertSame(NO_MATCH, NO_MATCH.and(MATCH));
    }

    @Test
    public void matchAnd() throws Exception {
        assertSame(TOO_MANY, MATCH.and(TOO_MANY));
        assertSame(NO_MATCH, MATCH.and(TOO_FEW));
        assertSame(NO_MATCH, MATCH.and(NO_MATCH));
        assertSame(MATCH, MATCH.and(MATCH));
    }

    @Test
    public void andIsCommutative() throws Exception {
        assertSame(TOO_MANY.and(TOO_FEW), TOO_FEW.and(TOO_MANY));
        assertSame(TOO_MANY.and(NO_MATCH), NO_MATCH.and(TOO_MANY));
        assertSame(TOO_MANY.and(MATCH), MATCH.and(TOO_MANY));

        assertSame(TOO_FEW.and(NO_MATCH), NO_MATCH.and(TOO_FEW));
        assertSame(TOO_FEW.and(MATCH), MATCH.and(TOO_FEW));

        assertSame(MATCH.and(NO_MATCH), NO_MATCH.and(MATCH));
    }
}