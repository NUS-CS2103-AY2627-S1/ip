package fein.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fein.FeinException;

/** Owns Fein's tasks and operations that change the task list. */
public class TaskList {
    /** The tasks currently managed by Fein. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param initialTasks the tasks to copy into the new list
     */
    public TaskList(List<Task> initialTasks) {
        // Loaded task data must exist, and each entry must be safe for list operations.
        assert initialTasks != null : "An initial task collection must be present";
        for (Task task : initialTasks) {
            assert task != null : "A task list must not contain null tasks";
        }
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the list unless it duplicates an existing task.
     *
     * @param task the task to add
     * @throws FeinException if the task duplicates an existing task
     */
    public void add(Task task) throws FeinException {
        // Null entries would break display, searching, and persistence invariants.
        assert task != null : "A task list must not contain null tasks";
        for (Task existingTask : tasks) {
            if (existingTask.hasSameDetailsAs(task)) {
                throw new FeinException("That task is already on your list. Try `list` to check it.");
            }
        }
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the task at the specified index
     */
    public Task get(int index) {
        // Internal callers convert user-facing one-based numbers before accessing the list.
        assert index >= 0 && index < tasks.size() : "A task index must refer to an existing task";
        return tasks.get(index);
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether there are no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Removes and returns the task at a one-based task number.
     *
     * @param taskNumber the one-based task number
     * @return the removed task
     * @throws FeinException if the task number does not refer to a task
     */
    public Task delete(int taskNumber) throws FeinException {
        validateTaskNumber(taskNumber);
        // Validation above guarantees that conversion to a zero-based index is safe.
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "Validated task numbers must refer to an existing task";
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Marks the task at a one-based task number as done.
     *
     * @param taskNumber the one-based task number
     * @return the marked task
     * @throws FeinException if the task number does not refer to a task
     */
    public Task mark(int taskNumber) throws FeinException {
        validateTaskNumber(taskNumber);
        // Validation above guarantees that conversion to a zero-based index is safe.
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "Validated task numbers must refer to an existing task";
        Task task = tasks.get(taskNumber - 1);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at a one-based task number as not done.
     *
     * @param taskNumber the one-based task number
     * @return the unmarked task
     * @throws FeinException if the task number does not refer to a task
     */
    public Task unmark(int taskNumber) throws FeinException {
        validateTaskNumber(taskNumber);
        // Validation above guarantees that conversion to a zero-based index is safe.
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "Validated task numbers must refer to an existing task";
        Task task = tasks.get(taskNumber - 1);
        task.markAsNotDone();
        return task;
    }

    /** Returns a defensive copy for persistence. */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword the text to search for
     * @return the matching tasks in their original order
     */
    public List<Task> find(String keyword) {
        // Fein validates that a find command contains a keyword before calling this method.
        assert keyword != null && !keyword.isBlank() : "A search keyword must be non-blank";
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /** Checks that a one-based task number refers to an existing task. */
    private void validateTaskNumber(int taskNumber) throws FeinException {
        if (taskNumber <= 0) {
            throw new FeinException("Task numbers start at 1. Try `list` to check the task number.");
        }
        if (taskNumber > tasks.size()) {
            throw new FeinException("I couldn't find task " + taskNumber
                    + ". Try `list` to check the task number.");
        }
    }
}
