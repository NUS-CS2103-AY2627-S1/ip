package snoopy.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Owns Snoopy's ordered collection of tasks and its collection operations.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list with its own collection containing the supplied tasks.
     *
     * @param tasks Initial tasks in display order.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must be provided";
        assert !tasks.contains(null) : "Initial task list must not contain null entries";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks.
     *
     * @return Current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a task by its zero-based index.
     *
     * @param index Zero-based task index.
     * @return Task at the requested index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Appends a task to the list.
     *
     * @param task Task to append.
     */
    public void add(Task task) {
        assert task != null : "Task to add must be provided";
        tasks.add(task);
    }

    /**
     * Inserts a task at a specified index.
     *
     * @param index Zero-based insertion index.
     * @param task Task to insert.
     */
    public void add(int index, Task task) {
        assert task != null : "Task to insert must be provided";
        tasks.add(index, task);
    }

    /**
     * Removes and returns a task by its zero-based index.
     *
     * @param index Zero-based task index.
     * @return Removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Finds tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Case-sensitive description keyword.
     * @return Matching tasks in their original order.
     */
    public TaskList find(String keyword) {
        assert keyword != null : "Search keyword must be provided";
        List<Task> matches = tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .toList();
        return new TaskList(matches);
    }

    /**
     * Returns a stream over the tasks without exposing the mutable collection.
     *
     * @return Stream of tasks in list order.
     */
    public Stream<Task> stream() {
        return tasks.stream();
    }
}
