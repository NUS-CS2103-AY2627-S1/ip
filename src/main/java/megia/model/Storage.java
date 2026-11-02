package megia.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import megia.service.LocalizationService;

/**
 * Stores an ordered collection of items in memory.
 *
 * @param <T> Type of item stored.
 */
public class Storage<T> {
    /** Items held by this storage in insertion order. */
    protected final List<T> items;

    /**
     * Creates an empty storage collection.
     */
    public Storage() {
        items = new ArrayList<>(100);
    }

    /**
     * Adds an item to the end of the collection.
     *
     * @param item Item to add.
     */
    public void add(T item) {
        items.add(item);
    }

    @Override
    public String toString() {
        return items.isEmpty()
                ? LocalizationService.getMessage("storage_empty")
                : LocalizationService.getMessage("storage_list") + "\n"
                        + IntStream.range(0, items.size())
                                .mapToObj(i -> (i + 1) + ". " + items.get(i) + "\n")
                                .collect(Collectors.joining())
                                .strip();
    }
}
