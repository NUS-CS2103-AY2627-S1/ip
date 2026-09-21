package sel.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents the list of tasks currently tracked by the application,
 * with operations to add, remove, mark, and query tasks by index.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates a new, empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by the given list, e.g. one loaded from
     * disk by {@link sel.storage.Storage}.
     *
     * @param tasks the initial tasks to populate this list with.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index the zero-based index of the task to remove.
     * @return the removed task.
     * @throws AssertionError if the index is invalid and assertions are enabled.
     */
    public Task delete(int index) {
        assert isValidIndex(index) : "Task index must be valid!";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index the zero-based index of the task to retrieve.
     * @return the task at that index.
     * @throws AssertionError if the index is invalid and assertions are enabled.
     */
    public Task get(int index) {
        assert isValidIndex(index) : "Task index must be valid!";
        return tasks.get(index);
    }

    /**
     * Finds an existing task that describes the same thing as the given
     * one, so that a duplicate can be reported instead of being added.
     *
     * @param candidate the task about to be added.
     * @return the first matching task, or an empty {@code Optional} if the
     *     candidate is not a duplicate.
     */
    public Optional<Task> findDuplicateOf(Task candidate) {
        return tasks.stream()
                .filter(task -> task.hasSameDetailsAs(candidate))
                .findFirst();
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks whether the given index refers to an existing task.
     *
     * @param index the zero-based index to check.
     * @return {@code true} if the index is within range, {@code false} otherwise.
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Marks the task at the given index as done.
     *
     * @param index the zero-based index of the task to mark.
     * @throws AssertionError if the index is invalid and assertions are enabled.
     */
    public void mark(int index) {
        assert isValidIndex(index) : "Task index must be valid!";
        tasks.get(index).mark();
    }

    /**
     * Marks the task at the given index as not done.
     *
     * @param index the zero-based index of the task to unmark.
     * @throws AssertionError if the index is invalid and assertions are enabled.
     */
    public void unmark(int index) {
        assert isValidIndex(index) : "Task index must be valid!";
        tasks.get(index).unmark();
    }

    /**
     * Returns the underlying list of tasks. The returned list is live;
     * changes to it are reflected in this TaskList, e.g. so
     * {@link sel.storage.Storage} can persist the current tasks.
     *
     * @return the underlying list of tasks.
     */
    public List<Task> asList() {
        return tasks;
    }

    /**
     * Returns all tasks whose description contains the given keyword
     * (case-insensitive, substring match).
     *
     * @param keyword the keyword to search for.
     * @return a new list of matching tasks, in their original order.
     */
    public List<Task> find(String keyword) {
        // Locale.ROOT, not the machine's locale: a plain toLowerCase() uses
        // the default locale, and in Turkish or Azerbaijani that turns "I"
        // into the dotless "\u0131". Searching "bike" would then never match a
        // task called "BIKE ride" for those users.
        String[] keywords = keyword.toLowerCase(Locale.ROOT).trim().split("\\s+");

        return tasks.stream()
                .filter(task -> {
                    String description = task.getDescription().toLowerCase(Locale.ROOT);
                    return Arrays.stream(keywords).allMatch(description::contains);
                })
                .toList();
    }
}
