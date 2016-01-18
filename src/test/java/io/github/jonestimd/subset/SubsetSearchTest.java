package io.github.jonestimd.subset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import org.junit.Test;

import static java.util.Collections.*;
import static org.junit.Assert.*;

public class SubsetSearchTest {
    private static Function<BigDecimal, BigDecimal> NOOP_ADAPTER = container -> container;

    private List<List<BigDecimal>> subSets(List<BigDecimal> items, int subTotal) {
        return subSet(items, new BigDecimal(subTotal));
    }

    private List<List<BigDecimal>> subSet(List<BigDecimal> items, BigDecimal subTotal) {
        return SubsetSearch.uniformSign(new BigDecimalSubsetPredicate<>(NOOP_ADAPTER, subTotal)).findSubSets(items);
    }

    @Test
    public void returnAllItems() throws Exception {
        List<BigDecimal> items = Arrays.asList(BigDecimal.TEN, new BigDecimal("20"));

        List<List<BigDecimal>> subSets = subSets(items, 30);

        assertEquals(1, subSets.size());
        assertEquals(items, subSets.get(0));
    }

    @Test @SuppressWarnings("unchecked")
    public void returnSingleItem() throws Exception {
        List<BigDecimal> items = Arrays.asList(BigDecimal.TEN, new BigDecimal("20"), new BigDecimal("30"));

        checkResult(subSets(singletonList(BigDecimal.TEN), 10), singletonList(BigDecimal.TEN));
        checkResult(subSets(items, 10), items.subList(0, 1));
        checkResult(subSets(items, 20), items.subList(1, 2));
        checkResult(subSets(items, 30), items.subList(0, 2), items.subList(2, 3));
    }

    @Test @SuppressWarnings("unchecked")
    public void returnTwoOfThree() throws Exception {
        List<BigDecimal> items = Arrays.asList(BigDecimal.TEN, new BigDecimal("20"), new BigDecimal("30"));

        checkResult(subSets(items, 40), Arrays.asList(items.get(0), items.get(2)));
        checkResult(subSets(items, 50), items.subList(1, 3));
    }

    @SuppressWarnings("unchecked")
    private void checkResult(List<List<BigDecimal>> actual, List<BigDecimal> ... expected) {
        assertEquals(expected.length, actual.size());
        for (int i=0; i<actual.size(); i++) {
            check1Result(actual.get(i), expected[i]);
        }
    }

    private void check1Result(List<BigDecimal> actual, List<BigDecimal> expected) {
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test @SuppressWarnings("unchecked")
    public void returnThreeOfFive() throws Exception {
        List<BigDecimal> items = Arrays.asList(BigDecimal.TEN, new BigDecimal("11"), new BigDecimal("13"),
                new BigDecimal("17"), new BigDecimal("25"));

        checkResult(subSets(items, 34), items.subList(0, 3));
        checkResult(subSets(items, 41), items.subList(1, 4));
        checkResult(subSets(items, 55), items.subList(2, 5));
        checkResult(subSets(items, 40), Arrays.asList(items.get(0), items.get(2), items.get(3)));
        checkResult(subSets(items, 48), Arrays.asList(items.get(0), items.get(2), items.get(4)));
        checkResult(subSets(items, 38), Arrays.asList(items.get(0), items.get(1), items.get(3)),
                Arrays.asList(items.get(2), items.get(4)));
        checkResult(subSets(items, 46), Arrays.asList(items.get(0), items.get(1), items.get(4)));
    }

    @Test
    public void noMatch() throws Exception {
        List<BigDecimal> items = Arrays.asList(BigDecimal.TEN, new BigDecimal("11"), new BigDecimal("13"),
                new BigDecimal("17"), new BigDecimal("25"));

        assertTrue(subSets(items, 9).isEmpty());
        assertTrue(subSets(items, 12).isEmpty());
        assertTrue(subSets(items, 77).isEmpty());
    }

    @Test
    public void randomSubsets() throws Exception {
        testRandomSubset(5, 10);
        testRandomSubset(2, 10);
        testRandomSubset(6, 15); // performance rapidly degrades for more than 20
        testRandomSubset(3, 7);
    }

    @SuppressWarnings("unchecked")
    private void testRandomSubset(int expectedSize, int totalSize) throws Exception {
        Random random = new Random();
        BigDecimal subtotal = BigDecimal.ZERO;
        List<BigDecimal> expected = new ArrayList<>(expectedSize);
        for (int i=0; i<expectedSize; i++) {
            BigDecimal item = new BigDecimal(random.nextDouble());
            subtotal = subtotal.add(item);
            expected.add(item);
        }
        List<BigDecimal> items = new ArrayList<>(totalSize);
        items.addAll(expected);
        for (int i=0; i<totalSize - expectedSize; i++) {
            items.add(new BigDecimal(random.nextDouble()));
        }
        Collections.shuffle(items);

        checkResult(subSet(items, subtotal), expected);
    }
}