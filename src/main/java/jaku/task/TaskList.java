package jaku.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jaku.JakuException;

/**
 * Stores Jaku tasks and provides operations on their one-based task numbers.
 */
public class TaskList {
    /** Tasks in the order they were added. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to include, in display order
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the supplied zero-based index.
     *
     * @param index zero-based index of the task to remove
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Re-inserts a task at its former zero-based index.
     *
     * @param index zero-based index at which to restore the task
     * @param task task to restore
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return the task at the index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Converts and validates a one-based task number to a zero-based index.
     *
     * @param taskNumber task number shown to the user
     * @return corresponding zero-based index
     * @throws JakuException if the task number is outside this list
     */
    public int getIndex(int taskNumber) throws JakuException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new JakuException("Task number " + taskNumber + " is not in the list.");
        }
        return taskNumber - 1;
    }

    /**
     * Returns tasks whose descriptions contain a keyword, ignoring letter case.
     *
     * @param keyword text to find within task descriptions
     * @return matching tasks in their original list order
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /**
     * Returns whether this list contains no tasks.
     *
     * @return true when the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a snapshot of tasks for display or persistence.
     *
     * @return tasks in display order
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
