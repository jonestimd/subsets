package io.github.jonestimd.subset;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

/**
 * Utilities for evaluating subsets of collections.
 */
public class Combinations {
    private Combinations() {}

    /**
     * Generate all possible subsets of {@code items} and pass them to {@code visitor}.
     */
    public static <T> void visitCombinations(Collection<T> items, CombinationVisitor<T> visitor) {
        visitCombinations(new ArrayList<>(items), new ArrayList<>(), visitor);
    }

    /**
     * Generate all possible subsets of {@code items} and pass them to {@code visitor}.
     */
    public static <T> void visitCombinations(List<T> items, CombinationVisitor<T> visitor) {
        visitCombinations(items, new ArrayList<>(), visitor);
    }

    private static <T> void visitCombinations(List<T> allItems, List<T> subset, CombinationVisitor<T> visitor) {
        Deque<Integer> stack = new ArrayDeque<>(allItems.size());
        stack.push(0);
        while (! stack.isEmpty()) {
            int index = stack.pop();
            while (index < allItems.size()) {
                T item = allItems.get(index);
                subset.add(item);
                index++;
                if (visitor.itemAdded(new ArrayList<>(subset), item)) {
                    stack.push(index);
                }
                else {
                    subset.remove(item);
                    visitor.itemRemoved(item);
                }
            }
            if (! subset.isEmpty()) {
                visitor.itemRemoved(subset.remove(subset.size()-1));
            }
        }
    }
}