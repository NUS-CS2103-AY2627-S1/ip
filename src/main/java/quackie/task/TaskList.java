package quackie.task;

import java.util.Arrays;
import java.util.Locale;

/**
 * Stores and manages the tasks in the current Quackie session.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;

    private final Task[] tasks;
    private int taskCount;

    /** Creates an empty task list with capacity for up to 100 tasks. */
    public TaskList() {
        tasks = new Task[MAX_TASKS];
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the number of stored tasks
     */
    public int size() {
        return taskCount;
    }

    /**
     * Adds a task when the list has capacity.
     *
     * @param task the task to add
     * @return {@code true} when the task was added, or {@code false} when the list is full
     */
    public boolean add(Task task) {
        assert task != null : "Task added to the list must not be null";

        if (taskCount >= tasks.length) {
            return false;
        }
        tasks[taskCount] = task;
        taskCount++;
        return true;
    }

    /**
     * Checks whether an equivalent task is already stored.
     *
     * @param task task whose details should be checked
     * @return {@code true} when a task with the same type and details exists
     */
    public boolean contains(Task task) {
        return containsExcept(task, -1);
    }

    /**
     * Checks for an equivalent task while ignoring one list position.
     *
     * @param task task whose details should be checked
     * @param ignoredIndex zero-based position to exclude, or {@code -1} to exclude none
     * @return {@code true} when another task with the same type and details exists
     */
    public boolean containsExcept(Task task, int ignoredIndex) {
        assert task != null : "Task checked for duplication must not be null";

        for (int i = 0; i < taskCount; i++) {
            if (i != ignoredIndex && tasks[i].hasSameDetails(task)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the task at the requested index
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public Task get(int index) {
        checkIndex(index);
        return tasks[index];
    }

    /**
     * Deletes and returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the deleted task
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public Task delete(int index) {
        checkIndex(index);
        Task removedTask = tasks[index];
        for (int i = index; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[--taskCount] = null;
        return removedTask;
    }

    /**
     * Replaces a task while retaining the original task's completion status.
     *
     * @param index the zero-based index of the task to replace
     * @param replacement the task containing the new details
     * @return the replacement task stored in the list
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public Task update(int index, Task replacement) {
        assert replacement != null : "Replacement task must not be null";

        Task original = get(index);
        if (original.isDone()) {
            replacement.markAsDone();
        }
        tasks[index] = replacement;
        return replacement;
    }

    /**
     * Marks the task at a zero-based index as done.
     *
     * @param index the zero-based task index
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public void markAsDone(int index) {
        get(index).markAsDone();
    }

    /**
     * Marks the task at a zero-based index as not done.
     *
     * @param index the zero-based task index
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public void markAsUndone(int index) {
        get(index).markAsUndone();
    }

    /**
     * Returns tasks whose descriptions contain the supplied keyword, ignoring case.
     *
     * @param keyword the text to search for in task descriptions
     * @return a new task list containing the matching tasks in their original order
     */
    public TaskList find(String keyword) {
        String normalisedKeyword = keyword.toLowerCase(Locale.ROOT);
        TaskList matches = new TaskList();
        Arrays.stream(tasks, 0, taskCount)
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalisedKeyword))
                .forEach(matches::add);
        return matches;
    }

    /**
     * Verifies that an index points to a stored task.
     *
     * @param index the zero-based task index to verify
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    private void checkIndex(int index) {
        if (index < 0 || index >= taskCount) {
            throw new IndexOutOfBoundsException("Invalid task index: " + index);
        }
    }
}
