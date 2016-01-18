package io.github.jonestimd.subset;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;

public class SubsetSumTest {
    @Test
    public void allMatches() throws Exception {
        List<Integer> items = Arrays.asList(1, 3, 4, 4, 5, 9);

        List<List<Integer>> subsets = SubsetSum.subsets(items, 13, -1);

        assertEquals(6, subsets.size());
        assertTrue(subsets.contains(Arrays.asList(4, 9)));
        assertTrue(subsets.contains(Arrays.asList(4, 4, 5)));
        assertTrue(subsets.contains(Arrays.asList(1, 3, 9)));
        assertTrue(subsets.contains(Arrays.asList(1, 3, 4, 5)));
    }

    @Test
    public void allMatches2() throws Exception {
        List<Integer> items = Arrays.asList(5, 6, 9, 15);

        List<List<Integer>> subsets = SubsetSum.subsets(items, 20, -1);

        assertEquals(2, subsets.size());
        assertTrue(subsets.contains(Arrays.asList(5, 6, 9)));
        assertTrue(subsets.contains(Arrays.asList(5, 15)));
    }

    @Test
    public void zeroSum() throws Exception {
        List<Integer> items = Arrays.asList(0, 1, 3, 4, 4, 5, 9);

        assertTrue(SubsetSum.subsets(items, 0, -1).isEmpty());
    }

    @Test
    public void noMatches() throws Exception {
        List<Integer> items = Arrays.asList(1, 3, 4, 4, 5, 9);

        assertTrue(SubsetSum.subsets(items, 2, -1).isEmpty());
        assertNull(SubsetSum.subset(items, 2));
    }

    @Test
    public void randomSubsetsOfInteger() throws Exception {
        testRandomSubset(5, 10, 100);
        testRandomSubset(2, 10, 100);
        testRandomSubset(20, 40, 1000);
        testRandomSubset(3, 7, 100);
    }

    private void testRandomSubset(int expectedSize, int totalSize, int maxItem) throws Exception {
        Random random = new Random();
        int subtotal = 0;
        List<Integer> items = new ArrayList<>(totalSize);
        for (int i=0; i<expectedSize; i++) {
            int item = Math.abs(random.nextInt(maxItem));
            items.add(item);
            subtotal += item;
        }
        int total = subtotal;
        for (int i=0; i<totalSize - expectedSize; i++) {
            int item = Math.abs(random.nextInt(maxItem));
            total += item;
            items.add(item);
        }
        Collections.shuffle(items);

        checkResult(SubsetSum.subset(items, subtotal), subtotal);
        checkResult(SubsetSum.subset(items, total - subtotal), total - subtotal);
        checkResults(SubsetSum.subsets(items, subtotal, 10), subtotal);
        checkResults(SubsetSum.subsets(items, total - subtotal, 10), total - subtotal);
    }

    private <T extends Number> void checkResults(List<List<T>> subsets, long subtotal) {
        assertNotNull("no subset found", subsets);
        assertFalse("no subset found", subsets.isEmpty());
        for (List<? extends Number> subset : subsets) {
            checkResult(subset, subtotal);
        }
    }

    private void checkResult(List<? extends Number> subset, long subtotal) {
        long actual = 0;
        for (Number item : subset) {
            actual += item.longValue();
        }
        assertEquals(subtotal, actual);
    }

    @Test
    public void randomSubsetsOfLong() throws Exception {
        testRandomSubset(5, 10, 100L);
        testRandomSubset(2, 10, 100L);
        testRandomSubset(20, 40, 1000L);
        testRandomSubset(3, 7, 100L);
    }

    private void testRandomSubset(int expectedSize, int totalSize, long maxItem) throws Exception {
        Random random = new Random();
        long subtotal = 0;
        List<Long> items = new ArrayList<>(totalSize);
        for (int i=0; i<expectedSize; i++) {
            long item = (long) (random.nextDouble() * maxItem);
            subtotal += item;
            items.add(item);
        }
        for (int i=0; i<totalSize - expectedSize; i++) {
            items.add((long) (random.nextDouble() * maxItem));
        }
        Collections.shuffle(items);

        checkResult(SubsetSum.subset(items, subtotal), subtotal);
        checkResults(SubsetSum.subsets(items, subtotal, 10), subtotal);
    }

    @Test
    public void randomSubsetsOfBigDecimal() throws Exception {
        testRandomSubset(5, 10, 100d);
        testRandomSubset(2, 10, 100d);
        testRandomSubset(20, 20, 1000d);
        testRandomSubset(40, 20, 1000d);
        testRandomSubset(3, 7, 100d);
    }

    private void testRandomSubset(int expectedSize, int totalSize, double maxItem) throws Exception {
        Random random = new Random();
        BigDecimal subtotal = BigDecimal.ZERO;
        List<BigDecimal> expected = new ArrayList<>(expectedSize);
        for (int i=0; i<expectedSize; i++) {
            BigDecimal item = BigDecimal.valueOf(random.nextDouble() * maxItem).setScale(6, RoundingMode.HALF_UP);
            subtotal = subtotal.add(item);
            expected.add(item);
        }
        List<BigDecimal> items = new ArrayList<>(totalSize);
        items.addAll(expected);
        for (int i=0; i<totalSize - expectedSize; i++) {
            items.add(BigDecimal.valueOf(random.nextDouble()* maxItem).setScale(6, RoundingMode.HALF_UP));
        }
        Collections.shuffle(items);
        checkResult(SubsetSum.subset(subtotal, items), subtotal);
        checkResults(SubsetSum.subsets(subtotal, items, 2), subtotal);
    }

    private void checkResults(Collection<List<BigDecimal>> subsets, BigDecimal subtotal) {
        assertNotNull("no subset found", subsets);
        assertFalse("no subset found", subsets.isEmpty());
        for (List<BigDecimal> subset : subsets) {
            checkResult(subset, subtotal);
        }
    }

    private void checkResult(List<BigDecimal> subset, BigDecimal subtotal) {
        BigDecimal actual = BigDecimal.ZERO;
        for (BigDecimal item : subset) {
            actual = actual.add(item);
        }
        assertEquals(0, subtotal.compareTo(actual));
    }
}