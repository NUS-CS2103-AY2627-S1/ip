package rene.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rene.exception.ReneException;

/**
 * Manages the tasks in Rene's current task list.
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
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks the initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        // A task is fully constructed and validated before it enters the list.
        assert task != null : "Task list entries must not be null";
        tasks.add(task);
    }

    /**
     * Marks a task as completed.
     *
     * @param taskNumber the one-based position of the task.
     * @return the task whose status changed.
     * @throws ReneException if the task number is invalid or the task is already done.
     */
    public Task mark(int taskNumber) throws ReneException {
        Task task = getByTaskNumber(taskNumber);
        if (task.isDone()) {
            throw new ReneException("This action item has already been completed — no action required.");
        }

        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as incomplete.
     *
     * @param taskNumber the one-based position of the task.
     * @return the task whose status changed.
     * @throws ReneException if the task number is invalid or the task is already incomplete.
     */
    public Task unmark(int taskNumber) throws ReneException {
        Task task = getByTaskNumber(taskNumber);
        if (!task.isDone()) {
            throw new ReneException("This action item has not yet been completed, so there is nothing to reopen.");
        }

        task.unmarkAsDone();
        return task;
    }

    /**
     * Removes a task from the list.
     *
     * @param taskNumber the one-based position of the task.
     * @return the removed task.
     * @throws ReneException if the task number is invalid.
     */
    public Task remove(int taskNumber) throws ReneException {
        int taskIndex = getTaskIndex(taskNumber);
        return tasks.remove(taskIndex);
    }

    /**
     * Returns a snapshot of the tasks in their current order.
     *
     * @return an unmodifiable copy of the task list.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Creates an independent copy of this task list and all its tasks.
     *
     * @return a copy that can be changed without affecting this list.
     */
    public TaskList copy() {
        return new TaskList(tasks.stream().map(Task::copy).toList());
    }

    /**
     * Returns tasks whose descriptions contain a keyword, ignoring letter case.
     *
     * @param keyword the text to search for in task descriptions.
     * @return the matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
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
     * @return {@code true} if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the task at a one-based position after validating it.
     */
    private Task getByTaskNumber(int taskNumber) throws ReneException {
        return tasks.get(getTaskIndex(taskNumber));
    }

    /**
     * Converts a valid one-based task number to its zero-based list index.
     */
    private int getTaskIndex(int taskNumber) throws ReneException {
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new ReneException("No action item with that position was found.");
        }

        // The guard above establishes this postcondition for every caller.
        assert taskIndex >= 0 && taskIndex < tasks.size() : "Validated task index must be in range";
        return taskIndex;
    }
}
