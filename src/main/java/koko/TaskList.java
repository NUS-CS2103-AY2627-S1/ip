package koko;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the list of tasks managed by koko.Koko.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the given tasks.
     *
     * @param tasks the tasks to store in the task list
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "A task list must be initialized with a task collection.";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "A task list cannot contain null tasks.";
        tasks.add(task);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index the index of the task
     * @return the requested task
     */
    public Task get(int index) {
        assert isValidIndex(index) : "A task index must refer to an existing task.";
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index the index of the task
     * @return the removed task
     */
    public Task remove(int index) {
        assert isValidIndex(index) : "A task index must refer to an existing task.";
        return tasks.remove(index);
    }

    /**
     * Replaces the task at the given zero-based index.
     *
     * @param index the index of the task to replace
     * @param task the replacement task
     */
    public void set(int index, Task task) {
        assert isValidIndex(index) : "A task index must refer to an existing task.";
        assert task != null : "A task list cannot contain null tasks.";
        tasks.set(index, task);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns all tasks in the list.
     *
     * @return the list of tasks
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns all tasks whose descriptions contain the given keyword.
     *
     * @param keyword the keyword to search for
     * @return the list of matching tasks
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Checks whether an index refers to a task currently in this list.
     *
     * @param index the zero-based task index to check
     * @return true if the index is valid
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}
