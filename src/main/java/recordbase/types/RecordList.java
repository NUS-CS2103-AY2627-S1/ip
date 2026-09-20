package recordbase.types;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import recordbase.exceptions.RecordException;

/**
 * Represents a collection of {@code ListItem} objects and provides operations for managing the items in the list.
 *
 * <p>The list supports adding, removing, retrieving, and updating the completion status of items.</p>
 */

public class RecordList {
    /**
     * Maximum number of tasks retained in one list.
     *
     * <p>The conservative bound prevents unbounded list growth and excessive memory use.</p>
     */
    public static final int MAX_ITEMS = 100_000;
    /**
     * Maximum number of characters retained in one task description.
     *
     * <p>This prevents a single pasted value from consuming excessive memory.</p>
     */
    public static final int MAX_DESCRIPTION_LENGTH = 10_000;
    private final ArrayList<ListItem> listItems;

    /**
     * Creates an empty {@code RecordList}.
     */
    public RecordList() {
        this.listItems = new ArrayList<>();
    }

    /**
     * Returns the {@code ListItem} at the specified index in the list.
     *
     * @param index the index of the item to retrieve
     * @return the {@code ListItem} at the specified index, or {@code null} if the index is out of bounds
     */
    public ListItem getItem(int index) {
        return (index >= 0 && index < this.listItems.size()) ? this.listItems.get(index) : null;
    }

    /**
     * Adds the specified item to the end of the list.
     *
     * @param item the item to add to the list
     * @return the index of the newly created item
     */
    public int addItem(ListItem item) {
        assert item != null : "List item must not be null";

        ensureCapacityFor(item.getTaskDescription());
        this.listItems.add(item);
        return this.listItems.size() - 1;
    }

    /**
     * Removes the item at the specified index from the list.
     *
     * @param index the index of the item to remove
     * @return the string description of the removed item
     * @throws RecordException if the specified index is out of bounds
     */
    public String deleteItem(int index) {
        if (index >= 0 && index < this.listItems.size()) {
            String deletedItemDescription = this.listItems.get(index).toString();
            this.listItems.remove(index);
            return deletedItemDescription;
        } else {
            throw new RecordException("That track number is not on your list.");
        }
    }

    /**
     * Adds an {@code EventItem} with the specified task description and event duration to end of the list.
     *
     * @param task the description of the task
     * @param fromDate the date and time when the event starts
     * @param toDate the date and time when the event ends
     * @return the index of the newly added event item
     */
    public int addEventItem(String task, LocalDateTime fromDate, LocalDateTime toDate) {
        return addEventItem(task, fromDate, toDate, Priority.MEDIUM);
    }

    /**
     * Adds an event with the specified priority.
     *
     * @param task the description of the task
     * @param fromDate the date and time when the event starts
     * @param toDate the date and time when the event ends
     * @param priority task priority
     * @return zero-based index of the newly added event
     * @throws RecordException if a configured task or description limit is exceeded
     */
    public int addEventItem(String task, LocalDateTime fromDate, LocalDateTime toDate, Priority priority) {
        return addItem(new EventItem(task, fromDate, toDate, priority));
    }
    /**
     * Adds an {@code DeadlineItem} with the specified task description and deadline to end of the list.
     *
     * @param task the description of the task
     * @param byDate the date and time which the task should be completed
     * @return the index of the newly added deadline item
     */
    public int addDeadlineItem(String task, LocalDateTime byDate) {
        return addDeadlineItem(task, byDate, Priority.MEDIUM);
    }

    /**
     * Adds a deadline with the specified priority.
     *
     * @param task the description of the task
     * @param byDate the date and time by which the task should be completed
     * @param priority task priority
     * @return zero-based index of the newly added deadline
     * @throws RecordException if a configured task or description limit is exceeded
     */
    public int addDeadlineItem(String task, LocalDateTime byDate, Priority priority) {
        return addItem(new DeadlineItem(task, byDate, priority));
    }
    /**
     * Adds an {@code ToDoItem} with the specified task description to end of the list.
     *
     * @param task the description of the task
     * @return the index of the newly added to-do item
     */
    public int addToDoItem(String task) {
        return addToDoItem(task, Priority.MEDIUM);
    }

    /**
     * Adds a to-do task with the specified priority.
     *
     * @param task the description of the task
     * @param priority task priority
     * @return zero-based index of the newly added to-do
     * @throws RecordException if a configured task or description limit is exceeded
     */
    public int addToDoItem(String task, Priority priority) {
        return addItem(new ToDoItem(task, priority));
    }

    /**
     * Adds a scheduled to-do task with the specified priority.
     *
     * @param task task description
     * @param scheduledDate planned date and time
     * @param priority task priority
     * @return zero-based index of the added task
     * @throws RecordException if a configured task or description limit is exceeded
     */
    public int addToDoItem(String task, LocalDateTime scheduledDate, Priority priority) {
        return addItem(new ToDoItem(task, scheduledDate, priority));
    }

    /**
     * Validates list and description bounds before retaining user-controlled text.
     *
     * @param description description about to be retained
     * @throws RecordException if the description or list has reached its limit
     */
    private void ensureCapacityFor(String description) {
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new RecordException("Task descriptions cannot exceed "
                    + MAX_DESCRIPTION_LENGTH + " characters.");
        }
        if (listItems.size() >= MAX_ITEMS) {
            throw new RecordException("The task limit of " + MAX_ITEMS
                    + " has been reached. Delete an item before adding another.");
        }
    }

    /**
     * Marks the item at the specified index as done.
     *
     * @param index the index of the item to mark as done
     * @return the string containing a completion confirmation and the marked item's details
     * @throws RecordException if the specified index is out of bounds
     */
    public String setListItemDone(int index) {
        return updateCompletionStatus(index, true);
    }

    /**
     * Marks the item at the specified index as not done.
     *
     * @param index the index of the item to mark as not done
     * @return the string containing a completion confirmation and the unmarked item's details
     * @throws RecordException if the specified index is out of bounds
     */
    public String setListItemNotDone(int index) {
        return updateCompletionStatus(index, false);
    }

    /**
     * Updates the completion status of an item and returns a confirmation message.
     *
     * @param index the index of the item to update
     * @param shouldMarkAsDone whether the item should be marked as completed
     * @return the confirmation containing the updated item
     * @throws RecordException if the specified index is out of bounds
     */
    private String updateCompletionStatus(int index, boolean shouldMarkAsDone) {
        if (index < 0 || index >= this.listItems.size()) {
            String action = shouldMarkAsDone ? "mark" : "unmark";
            throw new RecordException(String.format("I can't %s that track — it is not on your list.", action));
        }

        ListItem item = this.listItems.get(index);
        if (shouldMarkAsDone) {
            item.setDone();
        } else {
            item.setNotDone();
        }

        String response = shouldMarkAsDone ? "That one's complete and back in its sleeve:" : "Back in rotation:";
        return String.format("%s%n%s", response, item);
    }

    /**
     * Returns all items in the current list.
     *
     * @return an unmodifiable list containing all items in insertion order
     */
    public List<ListItem> getItems() {
        return List.copyOf(this.listItems);
    }

    /**
     * Searches the description of all tasks in the current list for a specific string.
     *
     * @param searchStr case-insensitive text to find in rendered task descriptions
     * @return mutable list containing every matching task in display order
     */
    public ArrayList<ListItem> searchItems(String searchStr) {
        assert searchStr != null : "Search term must not be null";

        ArrayList<ListItem> matches = new ArrayList<>();
        String searchTermLowercase = searchStr.toLowerCase();

        if (this.listItems != null) {
            for (ListItem item : this.listItems) {
                if (item.toString().toLowerCase().contains(searchTermLowercase)) {
                    matches.add(item);
                }
            }
        }

        return matches;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        if (!this.listItems.isEmpty()) {
            for (int i = 0; i < this.listItems.size(); i++) {
                sb.append(i + 1);
                sb.append(". ");
                sb.append(this.listItems.get(i));
                sb.append("\n");
            }

            return sb.toString();
        } else {
            return "The record is quiet — no tasks yet.";
        }
    }
}
