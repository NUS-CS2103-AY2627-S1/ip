package kiki.task;

import java.util.Arrays;
import java.util.Comparator;

import kiki.exception.KikiException;

/**
 * Manages the in-memory collection of {@link Task}s, backed by a fixed-size
 * array. Exposes the simple operations Kiki needs to add, remove, fetch,
 * and count tasks.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;

    private final Task[] tasks;
    private int taskCount;

    /**
     * Creates a new, empty task list.
     */
    public TaskList() {
        this.tasks = new Task[MAX_TASKS];
        this.taskCount = 0;
    }

    /**
     * Throws if the list is already at capacity, so callers can check
     * before doing any work to build the task that would be added.
     */
    public void ensureCanAdd() throws KikiException {
        if (isFull()) {
            throw new KikiException("Your task list is full.");
        }
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";
        assert !isFull() : "Caller should check capacity before adding";

        int previousTaskCount = taskCount;

        tasks[taskCount] = task;
        taskCount++;

        assert taskCount == previousTaskCount + 1 : "Task count should increase by one";
        assert tasks[previousTaskCount] == task : "Added task should be stored at the previous end";
    }

    /**
     * Removes the task at the given index, shifting later tasks up by one
     * position to close the gap.
     *
     * @param index Index of the task to remove.
     * @return The removed task.
     */
    public Task remove(int index) {
        assert index >= 0 && index < taskCount : "Index to remove should be within task list bounds";

        Task removed = tasks[index];

        for (int i = index; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }

        tasks[taskCount - 1] = null;
        taskCount--;

        assert tasks[taskCount] == null : "Removed task slot should be cleared";

        return removed;
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Index of the task to return.
     * @return The task at the requested index.
     */
    public Task get(int index) {
        assert index >= 0 && index < taskCount : "Index to get should be within task list bounds";

        return tasks[index];
    }

    /**
     * Sorts the stored tasks using the supplied ordering.
     *
     * @param comparator Ordering to use for the current tasks.
     */
    public void sort(Comparator<Task> comparator) {
        Arrays.sort(tasks, 0, taskCount, comparator);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return Number of stored tasks.
     */
    public int size() {
        return taskCount;
    }

    /**
     * Checks whether this list has reached its capacity.
     *
     * @return True if no more tasks can be added.
     */
    public boolean isFull() {
        return taskCount >= MAX_TASKS;
    }

    /**
     * Returns the maximum number of tasks this list can store.
     *
     * @return Maximum task capacity.
     */
    public int getCapacity() {
        return MAX_TASKS;
    }
}
