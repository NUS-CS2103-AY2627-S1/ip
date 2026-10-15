package tblade.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import tblade.exception.TBladeException;

/**
 * Holds the tasks currently known to the application and the operations that add to,
 * remove from, and enforce the capacity of that list.
 */
public class TaskList {
    private static final int MAX_TASKS = 100;

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Wraps an existing list of tasks, such as one just loaded from storage.
     *
     * @param tasks tasks to wrap
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Creates a task list containing exactly the given tasks, in order. A varargs convenience
     * for building a small fixed list in one line, e.g. a test fixture.
     *
     * @param tasks tasks to include
     * @return a new task list containing the given tasks
     */
    public static TaskList of(Task... tasks) {
        return new TaskList(new ArrayList<>(Arrays.asList(tasks)));
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     * @throws TBladeException if the list has reached its capacity, or an identical task
     *         (same type, description, and any date/time fields) is already in the list
     */
    public void add(Task task) throws TBladeException {
        if (tasks.size() >= MAX_TASKS) {
            throw new TBladeException("The task list already has " + MAX_TASKS + " tasks. "
                    + "You cannot add another task in this version.");
        }
        if (tasks.contains(task)) {
            throw new TBladeException("This task is already in your list: " + task
                    + ". Use `list` to see it, or give this one a different description.");
        }
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index zero-based index of the task to remove
     * @return the removed task
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size()
                : "index " + index + " out of bounds for size " + tasks.size()
                + ": callers must validate against size() first (e.g. via Parser.parseTaskIndex)";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index zero-based index of the task
     * @return the task at that index
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "index " + index + " out of bounds for size " + tasks.size()
                + ": callers must validate against size() first (e.g. via Parser.parseTaskIndex)";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying tasks, e.g. for saving to storage.
     *
     * @return the tasks in this list
     */
    public List<Task> getAll() {
        return tasks;
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     *
     * @param keyword text to search for within each task's description
     * @return a new task list containing only the matching tasks, in their original order
     */
    public TaskList find(String keyword) {
        List<Task> matches = tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(matches);
    }
}
