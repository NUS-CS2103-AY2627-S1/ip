package greenchonk.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the ordered collection of tasks and provides operations on it.
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
     * Creates a task list containing the supplied tasks in their current order.
     *
     * @param tasks the tasks with which to initialize the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list contains no tasks.
     *
     * @return true if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index the task's zero-based index.
     * @return the task at the index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns whether the list already contains a task with identical details.
     *
     * @param candidate the task whose details should be checked.
     * @return true if an equivalent task is already present.
     */
    public boolean containsEquivalent(Task candidate) {
        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
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
     * Inserts a task at a specified zero-based index.
     *
     * @param index the position at which to insert the task.
     * @param task the task to insert.
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Deletes and returns the task at a specified zero-based index.
     *
     * @param index the index of the task to delete.
     * @return the deleted task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Replaces and returns the task at a specified zero-based index.
     *
     * @param index the index of the task to replace.
     * @param task the replacement task.
     * @return the task previously stored at the index.
     */
    public Task replace(int index, Task task) {
        return tasks.set(index, task);
    }
}
