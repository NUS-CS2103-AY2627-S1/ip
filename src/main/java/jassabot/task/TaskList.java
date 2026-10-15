package jassabot.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Owns the application's ordered collection of tasks and its list operations.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks Initial tasks in display order.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task at the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Restores a task at a specific position, such as after a failed save.
     *
     * @param index Zero-based position at which to insert the task.
     * @param task Task to insert.
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Returns the task at a specific position.
     *
     * @param index Zero-based task position.
     * @return Task at the requested position.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a specific position.
     *
     * @param index Zero-based task position.
     * @return Removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks the task at a specific position as completed.
     *
     * @param index Zero-based task position.
     */
    public void mark(int index) {
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at a specific position as incomplete.
     *
     * @param index Zero-based task position.
     */
    public void unmark(int index) {
        tasks.get(index).markAsUndone();
    }

    /**
     * Finds tasks whose descriptions contain the supplied keyword, ignoring letter case.
     *
     * @param keyword Text to search for in task descriptions.
     * @return Unmodifiable matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /**
     * Returns the current number of tasks.
     *
     * @return Number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Provides a read-only view for displaying or saving the current tasks.
     *
     * @return Unmodifiable task view in display order.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
