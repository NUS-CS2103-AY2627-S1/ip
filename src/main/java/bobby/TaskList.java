package bobby;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Owns Bobby's ordered collection of tasks and its basic list operations. */
public class TaskList {
    /** Ordered tasks currently managed by this list. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the given tasks.
     *
     * @param tasks tasks to copy into the new list
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Task source list must not be null";
        for (Task task : tasks) {
            assert task != null : "Task lists must not contain null tasks";
        }
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of stored tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based position of the task
     * @return the task at {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is outside the list
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to append
     */
    public void add(Task task) {
        assert task != null : "Task lists must not contain null tasks";
        tasks.add(task);
    }

    /**
     * Inserts a task at the given zero-based index.
     *
     * @param index zero-based position at which to insert the task
     * @param task task to insert
     * @throws IndexOutOfBoundsException if {@code index} is outside the valid insertion range
     */
    public void add(int index, Task task) {
        assert task != null : "Task lists must not contain null tasks";
        tasks.add(index, task);
    }

    /**
     * Returns whether the list already contains a task with the same user-facing details.
     *
     * @param candidate task to compare with existing entries
     * @return {@code true} when an equivalent task is already present
     */
    public boolean containsTaskWithSameDetails(Task candidate) {
        assert candidate != null : "A duplicate-check candidate must not be null";
        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based position of the task to remove
     * @return the removed task
     * @throws IndexOutOfBoundsException if {@code index} is outside the list
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     * The returned tasks retain their order in this list.
     *
     * @param keyword text to search for
     * @return a new list containing the matching tasks
     */
    public List<Task> findByDescription(String keyword) {
        assert keyword != null : "Search keywords must not be null";
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(lowerCaseKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
