package kaya.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Owns Kaya's task collection and provides operations for accessing and changing it.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks the initial tasks
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        this.tasks = new ArrayList<>(tasks);
        assert !this.tasks.contains(null) : "Initial task list must not contain null tasks";
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index the zero-based task index
     * @return the task at the index
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be validated before retrieval";
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index the zero-based task index
     * @return the removed task
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be validated before deletion";
        return tasks.remove(index);
    }

    /**
     * Replaces a task without changing its position.
     *
     * @param index the validated zero-based index
     * @param task the replacement task
     */
    public void set(int index, Task task) {
        assert index >= 0 && index < tasks.size() : "Task index must be validated before replacement";
        assert task != null : "Replacement task must not be null";
        tasks.set(index, task);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword the text to search for
     * @return matching tasks in their original order
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ENGLISH).contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns a copy of the tasks suitable for display or saving.
     *
     * @return a copy of the stored tasks
     */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }

    /**
     * Copies the list and its tasks so later status changes cannot alter the snapshot.
     *
     * @return an independent snapshot for rolling back a failed command
     */
    public TaskList copy() {
        return new TaskList(tasks.stream().map(Task::copy).toList());
    }
}
