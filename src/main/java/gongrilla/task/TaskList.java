package gongrilla.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Owns the application's tasks and provides operations that modify them.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Creates a task list containing the supplied tasks in order.
     *
     * @param tasks initial tasks.
     */
    public TaskList(Task... tasks) {
        this(List.of(tasks));
    }

    /**
     * Adds the supplied tasks to the end of the list in order.
     *
     * @param tasks tasks to add.
     */
    public void addAll(Task... tasks) {
        this.tasks.addAll(List.of(tasks));
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns a task.
     *
     * @param index zero-based task index.
     * @return removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns a task without modifying it.
     *
     * @param index zero-based task index.
     * @return task at the index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Marks a task complete and returns it for display.
     *
     * @param index zero-based task index.
     * @return task that was marked complete.
     */
    public Task mark(int index) {
        Task task = get(index);
        task.markDone();
        return task;
    }

    /**
     * Marks a task incomplete and returns it for display.
     *
     * @param index zero-based task index.
     * @return task that was marked incomplete.
     */
    public Task unmark(int index) {
        Task task = get(index);
        task.unmarkDone();
        return task;
    }

    /**
     * Finds tasks whose descriptions contain a keyword, ignoring letter case.
     *
     * @param keyword text to find in task descriptions.
     * @return matching tasks in their original list order.
     * @throws NullPointerException if the keyword is {@code null}.
     * @throws IllegalArgumentException if the keyword is blank.
     */
    public List<Task> find(String keyword) {
        Objects.requireNonNull(keyword, "Search keyword cannot be null.");
        if (keyword.isBlank()) {
            throw new IllegalArgumentException("Search keyword cannot be blank.");
        }
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only snapshot for displaying tasks without exposing mutations.
     *
     * @return immutable view of the current tasks.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
