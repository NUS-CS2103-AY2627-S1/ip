package pixel.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the application's in-memory collection of tasks and its list operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in the same order.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "initial tasks must not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return Number of tasks currently stored.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks whether a zero-based index identifies an existing task.
     *
     * @param index Zero-based task index.
     * @return Whether the index is valid.
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return Task at the index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "task must not be null";
        tasks.add(task);
    }

    /**
     * Checks whether the list already contains a task with the same details.
     *
     * @param task Candidate task.
     * @return Whether an equivalent task is already present.
     */
    public boolean containsSameDetails(Task task) {
        return containsSameDetailsExcept(task, -1);
    }

    /**
     * Checks for an equivalent task while excluding one existing position.
     *
     * @param task Candidate task.
     * @param excludedIndex Index not to compare, such as the task being updated.
     * @return Whether another equivalent task is present.
     */
    public boolean containsSameDetailsExcept(Task task, int excludedIndex) {
        for (int i = 0; i < tasks.size(); i++) {
            if (i != excludedIndex && tasks.get(i).hasSameDetails(task)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return Removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Replaces the task at a zero-based index without changing list order.
     *
     * @param index Zero-based task index.
     * @param task Replacement task.
     */
    public void replace(int index, Task task) {
        assert task != null : "replacement task must not be null";
        tasks.set(index, task);
    }

    /**
     * Marks the task at a zero-based index as done.
     *
     * @param index Zero-based task index.
     */
    public void markAsDone(int index) {
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at a zero-based index as not done.
     *
     * @param index Zero-based task index.
     */
    public void markAsNotDone(int index) {
        tasks.get(index).markAsNotDone();
    }
}
