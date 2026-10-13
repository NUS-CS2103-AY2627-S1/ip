package arrodes.task;

import java.util.ArrayList;
import java.util.List;

import arrodes.exception.ArrodesException;


/** Bounded, ordered collection of tasks remembered by Arrodes. */
public class TaskList {
    /** Maximum number of tasks this list can contain. */
    private final int capacity;
    /** Tasks in the order in which they were added. */
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Creates an empty list with a chosen capacity.
     * @param capacity maximum number of tasks
     * @throws IllegalArgumentException if capacity is negative
     */
    public TaskList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Task list capacity cannot be negative.");
        }
        this.capacity = capacity;
        assert this.capacity >= 0 : "Task list capacity must be non-negative.";
    }

    /**
     * Returns the number of tasks currently stored.
     * @return current task count
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Returns whether no more tasks can be inserted.
     * @return {@code true} when the list has reached capacity
     */
    public boolean isFull() {
        return tasks.size() >= capacity;
    }

    /**
     * Adds a valid task to the end of the list.
     * @param task task to add
     * @throws ArrodesException if the task is invalid or the list is full
     */
    public void insert(Task task) throws ArrodesException {
        if (task == null || task.getDescription() == null || task.getDescription().isBlank()) {
            throw new ArrodesException(ArrodesException.EMPTY_DESCRIPTION);
        }
        if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetailsAs(task))) {
            throw new ArrodesException(ArrodesException.DUPLICATE_TASK);
        }
        if (isFull()) {
            throw new ArrodesException(ArrodesException.TASK_LIST_FULL);
        }
        tasks.add(task);
        assert tasks.size() <= capacity : "Task list must not exceed its capacity.";
    }

    /**
     * Removes a task using its one-based display number.
     * @param itemNumber one-based task number
     * @return the removed task
     * @throws ArrodesException if no task has that number
     */
    public Task delete(int itemNumber) throws ArrodesException {
        int itemIndex = itemNumber - 1;
        if (itemIndex >= tasks.size() || itemIndex < 0) {
            throw new ArrodesException(ArrodesException.ITEM_NOT_IN_LIST);
        }
        return tasks.remove(itemIndex);
    }

    /**
     * Restores a task at a specified zero-based position after a failed operation.
     *
     * @param itemIndex zero-based position at which to restore the task
     * @param task task to restore
     * @throws ArrodesException if the task or position is invalid, or the list is full
     */
    public void restore(int itemIndex, Task task) throws ArrodesException {
        if (task == null || itemIndex < 0 || itemIndex > tasks.size() || isFull()) {
            throw new ArrodesException(ArrodesException.ITEM_NOT_IN_LIST);
        }
        tasks.add(itemIndex, task);
    }

    /**
     * Returns a task using its zero-based list index.
     * @param itemIndex zero-based index
     * @return task at the requested index
     * @throws ArrodesException if the index is outside the list
     */
    public Task getTaskByIndex(int itemIndex) {
        if (itemIndex >= tasks.size() || itemIndex < 0) {
            throw new ArrodesException(ArrodesException.ITEM_NOT_IN_LIST);
        }
        Task task = tasks.get(itemIndex);
        assert task != null : "A valid task index must contain a task.";
        return task;
    }

    /**
     * Returns a task using its one-based display number.
     * @param itemNumber one-based task number
     * @return task at the requested number
     * @throws ArrodesException if the number is outside the list
     */
    public Task getTaskByNumber(int itemNumber) {
        return getTaskByIndex(itemNumber - 1);
    }
}
