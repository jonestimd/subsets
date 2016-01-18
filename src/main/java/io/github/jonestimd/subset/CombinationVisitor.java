package io.github.jonestimd.subset;

import java.util.List;

public interface CombinationVisitor<T> {
    /**
     * Notification that an item was added to the subset.
     * @param subset a copy of the current subset of items (including <code>item</code>).
     * @param item the last item added to the subset.
     * @return true to visit combinations that are a superset of <code>subset</code>.
     */
    boolean itemAdded(List<T> subset, T item);

    /**
     * Notification that an item was removed from the subset.
     */
    void itemRemoved(T item);
}
