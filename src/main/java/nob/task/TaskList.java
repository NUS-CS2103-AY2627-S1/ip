package nob.task;

import nob.exception.NobException;

/**
 * Stores Nob's tasks and provides operations for changing the list.
 */
public class TaskList {
    /** The tasks currently stored in the list. */
    private final Task[] tasks;

    /** The number of valid tasks in {@link #tasks}. */
    private int taskCount;

    /**
     * Creates an empty task list with the specified capacity.
     *
     * @param capacity Maximum number of tasks that can be stored.
     */
    public TaskList(int capacity) {
        assert capacity >= 0 : "Task-list capacity must not be negative";
        tasks = new Task[capacity];
        taskCount = 0;
    }

    /**
     * Creates a task list from the supplied task array and valid task count.
     *
     * @param tasks The initial task array.
     * @param taskCount Number of valid tasks in the array.
     */
    public TaskList(Task[] tasks, int taskCount) {
        assert tasks != null : "A task list requires a backing array";
        assert taskCount >= 0 && taskCount <= tasks.length
                : "The task count must fit within the backing array";
        for (int index = 0; index < taskCount; index++) {
            assert tasks[index] != null : "Every loaded task slot below the task count must be occupied";
        }
        this.tasks = tasks;
        this.taskCount = taskCount;
    }

    /**
     * Adds a task when the list has capacity.
     *
     * @param task The task to add.
     * @return Whether the task was added.
     */
    public boolean addTask(Task task) {
        assert task != null : "Only valid parsed tasks may be added";
        assert taskCount >= 0 && taskCount <= tasks.length
                : "The task count must remain within the backing array";
        if (taskCount == tasks.length) {
            return false;
        }
        tasks[taskCount] = task;
        taskCount++;
        return true;
    }

    /** Removes every task from the list. */
    public void clear() {
        for (int index = 0; index < taskCount; index++) {
            tasks[index] = null;
        }
        taskCount = 0;
    }

    /**
     * Marks the numbered task as complete or incomplete.
     *
     * @param taskNumber One-based task number.
     * @param isDone Whether to mark the task as complete.
     * @return The updated task.
     * @throws NobException If the task number is invalid.
     */
    public Task markTask(int taskNumber, boolean isDone) throws NobException {
        Task task = getTask(taskNumber);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
        return task;
    }

    /**
     * Removes and returns the numbered task.
     *
     * @param taskNumber One-based task number.
     * @return The removed task.
     * @throws NobException If the task number is invalid.
     */
    public Task deleteTask(int taskNumber) throws NobException {
        Task removedTask = getTask(taskNumber);
        for (int index = taskNumber - 1; index < taskCount - 1; index++) {
            tasks[index] = tasks[index + 1];
        }
        tasks[taskCount - 1] = null;
        taskCount--;
        return removedTask;
    }

    /**
     * Returns the numbered task.
     *
     * @param taskNumber One-based task number.
     * @return The matching task.
     * @throws NobException If the task number is invalid.
     */
    public Task getTask(int taskNumber) throws NobException {
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new NobException("Please enter a task number from 1 to " + taskCount + ".");
        }
        assert tasks[taskNumber - 1] != null : "Every numbered task slot must contain a task";
        return tasks[taskNumber - 1];
    }

    /** Returns the number of tasks in the list. */
    public int getTaskCount() {
        return taskCount;
    }

    /** Returns the task array for persistence. */
    public Task[] getTasks() {
        return tasks;
    }
}
