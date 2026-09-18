package martin.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Represents the list of tasks and encapsulates operations to manage and
 * manipulate tasks.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Constructs an empty {@code TaskList}.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a {@code TaskList} initialized with an existing list of tasks.
     *
     * @param tasks The initial list of tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Constructs a {@code TaskList} initialized with the specified tasks.
     *
     * @param tasks The initial tasks to populate the list with.
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>();
        Collections.addAll(this.tasks, tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null.";
        this.tasks.add(task);
    }

    /**
     * Removes the specified task from the list.
     *
     * @param task The task to remove.
     * @return {@code true} if the task was found and removed, {@code false}
     *         otherwise.
     */
    public boolean remove(Task task) {
        boolean removed = this.tasks.remove(task);
        assert !this.tasks.contains(task) : "Task is still present after remove(task).";
        return removed;
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index The zero-based index of the task to remove.
     * @return The removed task.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public Task remove(int index) {
        return this.tasks.remove(index);
    }

    /**
     * Retrieves the task at the specified zero-based index.
     *
     * @param index The zero-based index of the task.
     * @return The task at the specified index.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Returns the task selected by a one-based user index, or {@code null} if the
     * index is invalid.
     *
     * @param oneBasedIndex The one-based index of the task.
     * @return The selected {@code Task}, or {@code null} if invalid.
     */
    public Task getByOneBasedIndex(int oneBasedIndex) {
        if (oneBasedIndex < 1 || oneBasedIndex > this.tasks.size()) {
            return null;
        }
        return this.tasks.get(oneBasedIndex - 1);
    }

    /**
     * Returns the total number of tasks in the list.
     *
     * @return The number of tasks.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns whether the task list is empty.
     *
     * @return {@code true} if the task list is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return this.tasks.isEmpty();
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword,
     * ignoring letter case.
     *
     * @param keyword The keyword to search for.
     * @return The matching tasks in their original list order.
     * @throws IllegalArgumentException If {@code keyword} is {@code null}.
     */
    public List<Task> find(String keyword) {
        if (keyword == null) {
            throw new IllegalArgumentException("Search keyword cannot be null.");
        }

        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        if (normalizedKeyword.isEmpty()) {
            return List.of();
        }

        return this.tasks.stream()
                .filter(task -> task.description.toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /**
     * Returns an unmodifiable view of all tasks in the list.
     *
     * @return An unmodifiable list of tasks.
     */
    public List<Task> getAllTasks() {
        return Collections.unmodifiableList(this.tasks);
    }
}
