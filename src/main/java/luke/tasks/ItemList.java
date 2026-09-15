package luke.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import luke.datetime.DateTimeParser;

/**
 * Stores the items the user has added and knows how to present them as a
 * numbered list. This class deals only with the data; deciding how to display
 * it (colors, dividers, etc.) is left to the chatbot, so the two concerns can
 * change independently.
 */
public class ItemList {
    /**
     * Completion state shown in the second display bracket.
     */
    private enum Checkbox {
        DONE,
        NOT_DONE;

        private char getChar() {
            switch (this) {
                case DONE:
                    return 'X';
                case NOT_DONE:
                    return ' ';
                default:
                    throw new AssertionError("Unknown Checkbox value");
            }
        }
    }

    /**
     * One stored task and its display state.
     */
    private static class Item {
        private final String name;
        private final TaskTypes taskType;
        private Checkbox completed;
        private final EnumMap<Flag, String> flags;

        Item(String name, TaskTypes taskType, EnumMap<Flag, String> flags) {
            assert name != null && !name.isBlank() : "Item requires a description.";
            assert taskType != null : "Item requires a task type.";
            assert flags != null : "Item requires a flag map.";
            assert flags.keySet().equals(taskType.getFlags()) : "Item flags must match task type requirements.";
            this.name = name;
            this.taskType = taskType;
            this.completed = Checkbox.NOT_DONE;
            this.flags = flags;
        }

        private void setCompletion(boolean isDone) {
            this.completed = isDone ? Checkbox.DONE : Checkbox.NOT_DONE;
        }
    }

    private final List<Item> items = new ArrayList<>();

    /**
     * Creates an empty task list.
     */
    public ItemList() {
    }

    /**
     * Adds an item to the end of the list. Completion defaults to not done.
     *
     * @param name task description
     * @param taskType task type such as todo, deadline, or event
     * @param flags flag values associated with the task
     */
    public void add(String name, TaskTypes taskType, EnumMap<Flag, String> flags) {
        assert name != null && !name.isBlank() : "Cannot add an item without a description.";
        assert taskType != null : "Cannot add an item without a task type.";
        assert flags != null : "Cannot add an item without a flag map.";
        assert flags.keySet().equals(taskType.getFlags()) : "Item flags must match task type requirements.";
        items.add(new Item(name, taskType, flags));
    }

    /**
     * Removes the item at the given position.
     *
     * @param itemIndex 0-based index of the item to remove
     */
    public void remove(int itemIndex) {
        items.remove(itemIndex);
    }

    /**
     * Marks the item at the given position as done or not done.
     *
     * @param itemIndex 0-based index of the item (0 is the first item)
     * @param isDone  true to mark it done, false to mark it not done
     */
    public void setCompletion(int itemIndex, boolean isDone) {
        items.get(itemIndex).setCompletion(isDone);
    }

    /**
     * Returns whether the list has no items.
     *
     * @return {@code true} if no tasks have been added
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Returns the number of stored items.
     *
     * @return task count
     */
    public int size() {
        return items.size();
    }

    /**
     * Returns the description of the item at the given position.
     *
     * @param itemIndex 0-based index of the item
     * @return item description
     */
    public String getName(int itemIndex) {
        return items.get(itemIndex).name;
    }

    /**
     * Returns the task type of the item at the given position.
     *
     * @param itemIndex 0-based index of the item
     * @return item task type
     */
    public TaskTypes getTaskType(int itemIndex) {
        return items.get(itemIndex).taskType;
    }

    /**
     * Returns whether the item at the given position is marked as done.
     *
     * @param itemIndex 0-based index of the item
     * @return {@code true} if the item is done
     */
    public boolean isDone(int itemIndex) {
        return items.get(itemIndex).completed == Checkbox.DONE;
    }

    /**
     * Returns the flag values of the item at the given position.
     *
     * @param itemIndex 0-based index of the item
     * @return copy of the item's flag values
     */
    public EnumMap<Flag, String> getFlags(int itemIndex) {
        return new EnumMap<>(items.get(itemIndex).flags);
    }

    /**
     * Adds an item loaded from storage.
     *
     * @param name task description
     * @param taskType task type such as todo, deadline, or event
     * @param flags flag values associated with the task
     * @param isDone whether the loaded item is marked as done
     */
    public void addLoadedItem(
            String name,
            TaskTypes taskType,
            EnumMap<Flag, String> flags,
            boolean isDone) {
        add(name, taskType, flags);
        assert size() > 0 : "Loaded item should have been added before marking completion.";
        setCompletion(size() - 1, isDone);
    }

    /**
     * Formats flags for display after the task description.
     *
     * @param flags flag values to format
     * @return an empty string if there are no flags; otherwise a parenthesized string
     */
    public String formatFlags(EnumMap<Flag, String> flags) {
        if (flags.isEmpty()) {
            return "";
        } else {
            return " (%s)".formatted(
                    flags.entrySet().stream()
                            .map(entry -> "%s: %s".formatted(
                                    entry.getKey().name().toLowerCase(),
                                    entry.getValue()))
                            .collect(Collectors.joining(" "))
            );
        }
    }

    /**
     * Returns a single item formatted with its type, checkbox, description, and flags.
     *
     * @param itemIndex 0-based index of the item to format
     * @return the formatted item without a leading list number
     */
    public String formatOneItem(int itemIndex) {
        Item item = items.get(itemIndex);
        return "[%c][%c] %s%s".formatted(
                item.taskType.getChar(),
                item.completed.getChar(),
                item.name,
                formatFlags(item.flags)
        );
    }

    /**
     * Returns the items with checkboxes as a numbered list, one per line,
     * e.g. "1. [X] read book\n2. [ ] write essay".
     *
     * @return all items formatted as a numbered list
     */
    public String formatAllItems() {
        return formatNumberedItems(rangeIndexes());
    }

    /**
     * Returns the items sorted by the requested criterion.
     *
     * @param sortCriterion criterion used to order the items
     * @return all items formatted as a numbered list
     */
    public String formatAllItemsSorted(SortCriterion sortCriterion) {
        assert sortCriterion != null : "A sort criterion must be provided.";
        List<Integer> sortedIndexes = rangeIndexes().stream()
                .sorted(getItemComparator(sortCriterion))
                .toList();
        return formatNumberedItems(sortedIndexes);
    }

    /**
     * Returns the comparator for a supported task sort criterion. The original
     * item index provides a stable tie-breaker for equal sort values.
     *
     * @param sortCriterion criterion used to order the items
     * @return comparator for item indexes
     */
    private Comparator<Integer> getItemComparator(SortCriterion sortCriterion) {
        Comparator<Integer> comparator = switch (sortCriterion) {
            case ALPHA -> Comparator.comparing(
                    index -> items.get(index).name,
                    String.CASE_INSENSITIVE_ORDER);
            case TIME -> Comparator.comparing(this::getTimeSortKey);
        };
        return comparator.thenComparingInt(Integer::intValue);
    }

    /**
     * Returns items whose descriptions contain the search text.
     *
     * @param searchText text to search for
     * @return matching items formatted as a numbered list
     */
    public String formatItemsMatchingName(String searchText) {
        String normalizedSearchText = normalizeForSearch(searchText);
        List<Integer> matchingIndexes = rangeIndexes().stream()
                .filter(index -> normalizeForSearch(items.get(index).name)
                        .contains(normalizedSearchText))
                .toList();
        return formatNumberedItems(matchingIndexes);
    }

    private List<Integer> rangeIndexes() {
        return IntStream.range(0, items.size())
                .boxed()
                .toList();
    }

    /**
     * Formats selected item indexes as a numbered list.
     *
     * @param itemIndexes item indexes in the order they should be displayed
     * @return selected items formatted as numbered lines
     */
    private String formatNumberedItems(List<Integer> itemIndexes) {
        assert itemIndexes != null : "Item indexes must be provided.";
        return IntStream.range(0, itemIndexes.size())
                .mapToObj(position -> formatNumberedItem(position, itemIndexes))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Formats one selected item with its position in the displayed results.
     *
     * @param position 0-based position in the displayed results
     * @param itemIndexes item indexes in display order
     * @return one numbered display line
     */
    private String formatNumberedItem(int position, List<Integer> itemIndexes) {
        int itemIndex = itemIndexes.get(position);
        assert itemIndex >= 0 && itemIndex < items.size()
                : "Formatted item index should refer to an existing item.";
        return "%d. %s".formatted(position + 1, formatOneItem(itemIndex));
    }

    /**
     * Returns the first parseable date/time flag value of the item.
     *
     * @param itemIndex 0-based index of the item
     * @return parsed sort key, or {@link LocalDateTime#MAX} if none is parseable
     */
    private LocalDateTime getTimeSortKey(int itemIndex) {
        assert itemIndex >= 0 && itemIndex < items.size() : "Sort key index should refer to an existing item.";
        return items.get(itemIndex).flags.values().stream()
                .map(DateTimeParser::parseSortKey)
                .filter(sortKey -> sortKey != null)
                .findFirst()
                .orElse(LocalDateTime.MAX);
    }

    private String normalizeForSearch(String text) {
        return text.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}
