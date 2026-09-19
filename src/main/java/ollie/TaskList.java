package ollie;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Stores tasks and provides operations that modify their state.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing copies of the given task references.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     * @throws OllieException If an equivalent task is already stored.
     */
    public void add(Task task) throws OllieException {
        if (tasks.stream().anyMatch(task::hasSameDetails)) {
            throw new OllieException("That task is already in your list.");
        }
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index Zero-based task index.
     * @return Removed task.
     */
    public Task delete(int index) {
        assertValidIndex(index);
        return tasks.remove(index);
    }

    /**
     * Marks the task at the given zero-based index as completed.
     *
     * @param index Zero-based task index.
     */
    public void mark(int index) {
        assertValidIndex(index);
        tasks.get(index).mark();
    }

    /**
     * Marks the task at the given zero-based index as incomplete.
     *
     * @param index Zero-based task index.
     */
    public void unmark(int index) {
        assertValidIndex(index);
        tasks.get(index).unmark();
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks in their original order.
     */
    public TaskList find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();

        return new TaskList(matchingTasks);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index Zero-based task index.
     * @return Task at the index.
     */
    public Task get(int index) {
        assertValidIndex(index);
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable copy of the stored tasks.
     *
     * @return Copy of the stored tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    private void assertValidIndex(int index) {
        assert index >= 0 && index < tasks.size() : "Task index should be within list bounds";
    }
}
