package sace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Owns the collection of tasks and provides operations for accessing and changing it.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based task index.
     * @return selected task.
     */
    public Task get(int index) {
        assert isValidIndex(index) : "Task index must refer to an existing task";
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at a specific position when a failed operation must be rolled back.
     *
     * @param index zero-based insertion position.
     * @param task task to insert.
     */
    public void insert(int index, Task task) {
        assert index >= 0 && index <= tasks.size()
                : "Task insertion index must be within the list bounds";
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based task index.
     * @return removed task.
     */
    public Task delete(int index) {
        assert isValidIndex(index) : "Task index must refer to an existing task";
        return tasks.remove(index);
    }

    /**
     * Marks and returns the task at the given zero-based index.
     *
     * @param index zero-based task index.
     * @return marked task.
     */
    public Task mark(int index) {
        assert isValidIndex(index) : "Task index must refer to an existing task";
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks and returns the task at the given zero-based index.
     *
     * @param index zero-based task index.
     * @return unmarked task.
     */
    public Task unmark(int index) {
        assert isValidIndex(index) : "Task index must refer to an existing task";
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns tasks whose descriptions contain the given keyword, ignoring case.
     *
     * @param keyword keyword to search for.
     * @return matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
    }

    /**
     * Returns a read-only view of the tasks for persistence.
     *
     * @return read-only task list.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns whether an index identifies a task currently in the list.
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}
