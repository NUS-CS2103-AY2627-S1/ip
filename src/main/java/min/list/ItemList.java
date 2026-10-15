package min.list;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Stores items together with the subset of them currently shown to the user.
 * <p>
 * Commands that refer to an item by number use the displayed view, so that a number shown by a
 * search still refers to the same item when the user acts on it.
 *
 * @param <T> The type of item stored in this list.
 */
public abstract class ItemList<T> {
    private final List<T> items;
    private final List<T> displayedItems;
    private boolean isShowingAllItems;

    /**
     * Creates an item list whose initial displayed view contains every item.
     *
     * @param items The initial items.
     */
    protected ItemList(List<T> items) {
        assert items != null : "Initial item list must not be null.";

        this.items = new ArrayList<>(items);
        this.displayedItems = new ArrayList<>(items);
        this.isShowingAllItems = true;

        assert isDisplayedViewConsistent()
                : "Displayed items must be consistent with the full item list.";
    }

    /**
     * Adds an item to the list.
     *
     * @param item The item to add.
     */
    protected void add(T item) {
        assert item != null : "Added item must not be null.";

        this.items.add(item);
        if (this.isShowingAllItems) {
            this.displayedItems.add(item);
        }

        assert isDisplayedViewConsistent()
                : "Displayed items must be consistent with the full item list.";
    }

    /**
     * Sets the displayed view to the items accepted by the given test.
     *
     * @param isMatch The test that decides whether an item is shown.
     * @return A read-only list of matching items.
     */
    protected List<T> showMatching(Predicate<T> isMatch) {
        assert isMatch != null : "Match test must not be null.";

        List<T> matchingItems = this.items.stream()
                .filter(isMatch)
                .toList();

        this.displayedItems.clear();
        this.displayedItems.addAll(matchingItems);
        this.isShowingAllItems = false;

        assert isDisplayedViewConsistent()
                : "Displayed items must be consistent with the full item list.";

        return matchingItems;
    }

    /** Restores and returns the complete item list as the displayed view. */
    protected List<T> showAll() {
        this.displayedItems.clear();
        this.displayedItems.addAll(this.items);
        this.isShowingAllItems = true;

        assert isDisplayedViewConsistent()
                : "Displayed items must be consistent with the full item list.";

        return List.copyOf(this.displayedItems);
    }

    /**
     * Returns the displayed item at the given index without removing it.
     *
     * @param index The zero-based index in the displayed view.
     * @return The requested item.
     */
    protected T getDisplayedItem(int index) {
        assert index >= 0 && index < this.displayedItems.size()
                : "Item index must refer to a displayed item.";

        return this.displayedItems.get(index);
    }

    /**
     * Removes and returns the displayed item at the given index.
     *
     * @param index The zero-based index in the displayed view.
     * @return The removed item.
     */
    protected T delete(int index) {
        assert index >= 0 && index < this.displayedItems.size()
                : "Item index must refer to a displayed item.";

        T item = this.displayedItems.remove(index);
        boolean wasRemoved = this.items.remove(item);

        assert wasRemoved : "Displayed item must exist in the full item list.";
        assert isDisplayedViewConsistent()
                : "Displayed items must be consistent with the full item list.";

        return item;
    }

    /** Returns a read-only snapshot of the current items. */
    protected List<T> getItems() {
        return List.copyOf(this.items);
    }

    /** Returns the number of items in the list. */
    public int size() {
        return this.items.size();
    }

    /** Returns the number of items in the current displayed view. */
    protected int getDisplayedCount() {
        return this.displayedItems.size();
    }

    /**
     * Returns whether the displayed view is consistent with the full item list.
     *
     * @return Whether every displayed item belongs to the full item list and both lists
     *         match when all items are being shown.
     */
    private boolean isDisplayedViewConsistent() {
        return this.items.containsAll(this.displayedItems)
                && (!this.isShowingAllItems || this.items.equals(this.displayedItems));
    }
}
