package io.github.jonestimd.subset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

/**
 * For a collection, finds subsets having a specified sum.  The collection can contain numbers or a function can
 * be supplied to extract numbers from the collection items.
 * @param <V> the collection item type
 */
public class SubsetSum<V> {
    // used for initial sizing of collections
    private static final int EXPECTED_RESULTS = 5;

    // TODO replace subset tolerance with item tolerance and scale by subset size
    // TODO negative items
    // TODO disallow mixed sign items
    /**
     * For a collection, find the subsets having the specified sum.
     * @param sum the target sum for the subsets
     * @param itemTolerance precision of the items in the collection
     * @param items the collection to search
     * @param adapter a function to extract the value to sum from the collection items
     * @param maxResults the maximum number of subsets to return
     * @return the matching subsets
     */
    public static <T> List<List<T>> subsets(BigDecimal sum, BigDecimal itemTolerance, Collection<T> items, Function<? super T, BigDecimal> adapter, int maxResults) {
        BigDecimalToLong bigDecimalToLong = new BigDecimalToLong(maxScale(items.stream().map(adapter), sum, itemTolerance));
        return subsets(items, bigDecimalToLong.apply(sum), bigDecimalToLong.apply(itemTolerance), maxResults, bigDecimalToLong.compose(adapter));
    }

    /**
     * For a collection of {@link BigDecimal}, find the subsets having the specified sum.
     * @param sum the target sum for the subsets
     * @param items the collection to search
     * @param maxResults the maximum number of subsets to return
     * @return the matching subsets
     */
    public static List<List<BigDecimal>> subsets(BigDecimal sum, Collection<BigDecimal> items, int maxResults) {
        BigDecimalToLong adapter = new BigDecimalToLong(maxScale(items.stream(), sum));
        return subsets(items, adapter.apply(sum), 0L, maxResults, adapter);
    }

    /**
     * For a collection of {@link BigDecimal}, find the subsets having the specified sum.
     * @param sum the target sum for the subsets
     * @param items the collection to search
     * @return the matching subsets
     */
    public static List<BigDecimal> subset(BigDecimal sum, Collection<BigDecimal> items) {
        BigDecimalToLong adapter = new BigDecimalToLong(maxScale(items.stream(), sum));
        return subset(items, adapter.apply(sum), adapter);
    }

    private static int maxScale(Stream<BigDecimal> items, BigDecimal ... moreItems) {
        int maxScale = items.mapToInt(BigDecimal::scale).max().orElse(Integer.MIN_VALUE);
        for (BigDecimal value : moreItems) {
            maxScale = Math.max(maxScale, value.scale());
        }
        return maxScale;
    }

    /**
     * For a collection of numbers, find the subsets having the specified sum.
     * @param sum the target sum for the subsets
     * @param items the collection to search
     * @param maxResults the maximum number of subsets to return
     * @return the matching subsets
     */
    public static <T extends Number> List<List<T>> subsets(Collection<T> items, T sum, int maxResults) {
        return subsets(items, sum.longValue(), 0L, maxResults, Number::longValue);
    }

    /**
     * For a collection of numbers, find the subsets having the specified sum.
     * @param sum the target sum for the subsets
     * @param items the collection to search
     * @return the matching subsets
     */
    public static <T extends Number> List<T> subset(Collection<T> items, T sum) {
        return subset(items, sum.longValue(), Number::longValue);
    }

    private static <T> List<T> subset(Collection<T> items, long sum, Function<? super T, Long> adapter) {
        List<List<T>> sums = subsets(items, sum, 0L, 1, adapter);
        return sums.isEmpty() ? null : sums.get(0);
    }

    /**
     * For a collection, find the subsets having the specified sum.
     * @param items the collection to search
     * @param sum the target sum for the subsets
     * @param itemTolerance precision of the items in the collection
     * @param maxResults the maximum number of subsets to return
     * @param adapter a function to extract the value to sum from the collection items
     * @return the matching subsets
     */
    public static <T> List<List<T>> subsets(Collection<T> items, long sum, long itemTolerance, int maxResults, Function<? super T, Long> adapter) {
        return sum == 0 ? Collections.<List<T>>emptyList() : new SubsetSum<>(maxResults, items, adapter).findSubsets(sum, itemTolerance);
    }

    private final int maxResults;
    private final Collection<V> items;
    private final Function<? super V, Long> adapter;
    private final Predicate<V> notZero;
    private final long total;
    private final Map<Long, List<List<V>>> subsetSums = new TreeMap<>();

    private SubsetSum(int maxResults, Collection<V> items, Function<? super V, Long> adapter) {
        this.items = items;
        this.maxResults = maxResults;
        this.adapter = adapter;
        this.notZero = v -> adapter.apply(v) != 0L;
        this.total = getTotal();
    }

    private long getTotal() {
        long sum = 0L;
        for (V item : items) {
            sum += adapter.apply(item);
        }
        return sum;
    }

    private List<List<V>> findSubsets(long sum, long itemTolerance) {
        if (Math.abs(total - sum) <= itemTolerance * items.size()) {
            List<List<V>> subsets = new ArrayList<>(1);
            subsets.add(new ArrayList<>(items));
            return subsets;
        }
        if (sum > total / 2) {
            List<List<V>> subsets = getNearestMatch(total - sum, itemTolerance);
            for (int i = 0; i < subsets.size(); i++) {
                subsets.set(i, getComplement(subsets.get(i)));
            }
            return subsets;
        }
        return getNearestMatch(sum, itemTolerance);
    }

    private List<V> getComplement(List<V> toRemove) {
        List<V> result = new ArrayList<>(items);
        toRemove.forEach(result::remove);
        return result;
    }

    /**
     * Calculate subsets with a sum &lt;= {@code target + itemTolerance*items.size()} and return the closest match(es).
     * @param targetSum the target sum
     * @param itemTolerance precision of the collection item values
     * @return the subsets having the sum closest to the specified value
     */
    private List<List<V>> getNearestMatch(long targetSum, long itemTolerance) {
        buildSubsets(targetSum + itemTolerance * items.size());
        List<List<V>> subsets = subsetSums.get(targetSum);
        if (subsets == null) {
            long minDiff = Long.MAX_VALUE;
            for (Entry<Long, List<List<V>>> sumEntry : subsetSums.entrySet()) {
                long diff = Math.abs(targetSum - sumEntry.getKey());
                if (diff <= itemTolerance * sumEntry.getValue().size() && diff < minDiff) {
                    subsets = sumEntry.getValue();
                    minDiff = diff;
                }
            }
        }
        return subsets == null ? Collections.<List<V>>emptyList() : subsets;
    }

    /**
     * Populate {@link #subsetSums} with subsets having a sum &lt;= {@code maxSum}.
     */
    private void buildSubsets(long maxSum) {
        List<V> sortedItems = items.stream().filter(notZero).filter(lessThanOrEqual(adapter, maxSum))
                .sorted(Ordering.<Long>natural().onResultOf(adapter::apply))
                .collect(Collectors.toList());
        Multimap<Long, List<V>> sums = ArrayListMultimap.create(items.size()*3, maxResults <= 0 ? EXPECTED_RESULTS : maxResults);
        for (V item : sortedItems) {
            long longItem = adapter.apply(item);
            subsetSums.entrySet().stream().filter(lessThanOrEqual(Entry::getKey, maxSum - longItem)).forEach(sumEntry -> {
                long subsetSum = longItem + sumEntry.getKey();
                int count = count(subsetSum);
                for (List<V> subSubset : applyLimit(sumEntry.getValue(), count + sums.get(subsetSum).size())) {
                    List<V> subset = new ArrayList<>(subSubset.size() + 1);
                    subset.addAll(subSubset);
                    subset.add(item);
                    sums.put(subsetSum, subset);
                }
            });
            getSubsets(longItem).add(Collections.singletonList(item));
            appendSubsets(sums);
            sums.clear();
        }
    }

    private <T> Predicate<T> lessThanOrEqual(Function<T, Long> function, long max) {
        return v -> function.apply(v) <= max;
    }

    /**
     * Get an item from {@link #subsetSums}.  Adds an entry if none exists.
     */
    private List<List<V>> getSubsets(long sum) {
        List<List<V>> subsets = subsetSums.get(sum);
        if (subsets == null) {
            subsets = new ArrayList<>(maxResults <= 0 ? EXPECTED_RESULTS : maxResults);
            subsetSums.put(sum, subsets);
        }
        return subsets;
    }

    /**
     * Add subsets to an entry in {@link #subsetSums}.
     */
    private void appendSubsets(Multimap<Long, List<V>> toAppend) {
        for (Entry<Long, Collection<List<V>>> entry : toAppend.asMap().entrySet()) {
            getSubsets(entry.getKey()).addAll(entry.getValue());
        }
    }

    /**
     * Get the number of subsets for the specified sum.
     */
    private int count(long sum) {
        List<List<V>> list = subsetSums.get(sum);
        return list == null ? 0 : list.size();
    }

    private List<List<V>> applyLimit(List<List<V>> list, int existing) {
        return maxResults <= 0 ? list : list.subList(0, Math.min(list.size(), maxResults - existing));
    }

    /**
     * Converts {@link BigDecimal} to fixed-point long value.
     */
    private static class BigDecimalToLong implements Function<BigDecimal, Long> {
        private final int scale;

        public BigDecimalToLong(int scale) {
            this.scale = scale;
        }

        public Long apply(BigDecimal input) {
            return input.scaleByPowerOfTen(scale).longValueExact();
        }
    }
}