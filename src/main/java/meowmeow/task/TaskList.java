package meowmeow.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import meowmeow.MeowmeowException;

/**
 * The list of tasks Meowmeow is tracking, plus the operations that act on
 * it: adding, deleting, looking up by position, and filtering by date.
 *
 * <p>Positions are given as <em>1-based</em> indices - the same numbers a
 * user sees next to each task in "list" - and {@link #get(int)} /
 * {@link #delete(int)} validate them, throwing {@link MeowmeowException}
 * for a number outside the list. Keeping that check here means the command
 * handlers no longer each repeat it.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Constructs an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a task list holding the given tasks (e.g. the ones just
     * loaded from disk). The tasks are copied into a new list, so later
     * changes here do not write back through the caller's collection.
     */
    public TaskList(List<Task> initialTasks) {
        // Storage.load() returns an empty list (never null) for a first run, so
        // the only caller always has a real list to hand over.
        assert initialTasks != null : "initial task list should not be null";
        this.tasks = new ArrayList<>(initialTasks);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        // findOn() and findByKeyword() call methods on every element, so a null
        // task in the list would break them later, far from the real cause.
        assert task != null : "the task list must never hold a null task";
        tasks.add(task);
    }

    /**
     * Returns the task at the given 1-based position.
     *
     * @param oneBasedIndex the 1-based position, as shown by "list".
     * @return the task at that position.
     * @throws MeowmeowException if no task has that position.
     */
    public Task get(int oneBasedIndex) throws MeowmeowException {
        return tasks.get(toListIndex(oneBasedIndex));
    }

    /**
     * Removes and returns the task at the given 1-based position.
     *
     * @param oneBasedIndex the 1-based position, as shown by "list".
     * @return the task that was removed.
     * @throws MeowmeowException if no task has that position.
     */
    public Task delete(int oneBasedIndex) throws MeowmeowException {
        return tasks.remove(toListIndex(oneBasedIndex));
    }

    /**
     * Returns the tasks occurring on the given date - deadlines due that day
     * and events whose span covers it (see {@link Task#occursOn(LocalDate)}) -
     * in list order.
     */
    public List<Task> findOn(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .toList();
    }

    /**
     * Returns the tasks whose description contains {@code keyword}, in list
     * order. The match is case-insensitive and a substring test, so
     * "book" matches "Booking" and "return book" alike - consistent with
     * the rest of Meowmeow's case-insensitive input handling.
     *
     * @param keyword the text to look for inside each task's description.
     * @return the matching tasks, in list order; empty if none match.
     */
    public List<Task> findByKeyword(String keyword) {
        String needle = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(needle))
                .toList();
    }

    /**
     * Returns a read-only view of every task, in list order - for display and
     * for saving to disk. It is unmodifiable so callers change the list only
     * through this class's own methods.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Converts a user-facing 1-based position to a 0-based list index,
     * rejecting anything outside the list with the message Meowmeow has
     * always shown for a non-existent task.
     */
    private int toListIndex(int oneBasedIndex) throws MeowmeowException {
        if (oneBasedIndex < 1 || oneBasedIndex > tasks.size()) {
            throw new MeowmeowException(" Task " + oneBasedIndex + " doesn't exist in your list.");
        }

        int listIndex = oneBasedIndex - 1;
        // Records the postcondition: once the range check above has passed, the
        // converted index is always safe to use on the backing list.
        assert listIndex >= 0 && listIndex < tasks.size()
                : "range check should guarantee a valid list index";
        return listIndex;
    }
}
