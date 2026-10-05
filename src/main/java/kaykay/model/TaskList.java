package kaykay.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Stores the tasks that Kaykay manages and provides task-list operations.
 */
public class TaskList {
    /** The tasks in their current display order. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks tasks loaded from storage.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to add to the list.
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>(Arrays.asList(tasks));
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return the task at the index.
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword text to search for.
     * @return matching tasks in their current display order.
     */
    public List<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
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
     * Adds a task at a specific zero-based index.
     *
     * @param index zero-based insertion index.
     * @param task task to add.
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at a specific zero-based index.
     *
     * @param index zero-based task index.
     * @return the removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Removes the specified task from the list.
     *
     * @param task task to remove.
     */
    public void remove(Task task) {
        tasks.remove(task);
    }

    /**
     * Checks whether a value identifies an existing one-based task number.
     *
     * @param value task number entered by the user.
     * @return true if the value identifies a task in this list.
     */
    public boolean isValidTaskNumber(String value) {
        try {
            int taskNumber = Integer.parseInt(value);
            return taskNumber >= 1 && taskNumber <= tasks.size();
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
