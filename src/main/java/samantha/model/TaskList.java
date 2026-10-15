package samantha.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import samantha.exception.InputException;

/**
 * Stores and provides operations on Samantha's tasks.
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
     * Adds all supplied tasks to this task list.
     *
     * @param tasks tasks to add
     */
    public void addAll(Collection<Task> tasks) {
        assert tasks != null : "Tasks to add must not be null";
        assert tasks.stream().noneMatch(task -> task == null)
                : "A task list must not contain null tasks";
        this.tasks.addAll(tasks);
    }

    /**
     * Adds a task to the end of this task list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "A task list must not contain null tasks";
        tasks.add(task);
    }

    /**
     * Inserts a task at a zero-based position in this task list.
     *
     * @param task task to insert
     * @param index zero-based insertion position
     */
    public void addTaskAt(Task task, int index) {
        assert task != null : "A task list must not contain null tasks";
        assert index >= 0 && index <= tasks.size() : "Task index must be within insertion range";
        tasks.add(index, task);
    }

    /**
     * Returns the task identified by a one-based user-facing ID.
     *
     * @param taskId one-based task ID
     * @return the matching task
     * @throws InputException if the ID is outside this list
     */
    public Task getTask(int taskId) throws InputException {
        return tasks.get(getTaskIndex(taskId));
    }

    /**
     * Removes and returns the task identified by a one-based user-facing ID.
     *
     * @param taskId one-based task ID
     * @return the removed task
     * @throws InputException if the ID is outside this list
     */
    public Task removeTask(int taskId) throws InputException {
        return tasks.remove(getTaskIndex(taskId));
    }

    /**
     * Removes a specific task from this task list.
     *
     * @param task task to remove
     * @throws InputException if the task is not in this task list
     */
    public void removeTask(Task task) throws InputException {
        assert task != null : "A task to remove must not be null";
        if (!tasks.remove(task)) {
            throw new InputException("You entered a task number that does not exist.");
        }
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an immutable snapshot of the current tasks for persistence.
     *
     * @return snapshot of the tasks in this list
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Converts a valid one-based task ID into the list's internal index.
     *
     * @param taskId one-based task ID
     * @return zero-based index for the task ID
     * @throws InputException if the ID is outside this list
     */
    private int getTaskIndex(int taskId) throws InputException {
        if (taskId < 1 || taskId > tasks.size()) {
            throw new InputException("You entered a task number that does not exist.");
        }
        return taskId - 1;
    }
}
