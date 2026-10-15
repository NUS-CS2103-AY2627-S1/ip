package kdb;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Owns the collection of tasks and provides operations for changing it.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        assert task != null : "Task added to the list must not be null";
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based position of the task
     * @return task at the requested position
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based position of the task
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     * Matching is case-insensitive.
     *
     * @param keyword text to search for in task descriptions
     * @return matching tasks in their original order
     */
    public TaskList find(String keyword) {
        TaskList matchingTasks = new TaskList();
        String normalizedKeyword = keyword.toLowerCase();

        tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(normalizedKeyword))
                .forEach(matchingTasks::add);
        return matchingTasks;
    }

    /** Returns an iterator over the stored tasks. */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
