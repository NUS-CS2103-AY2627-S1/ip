package sherlock.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores Sherlock's tasks and provides indexed access to them.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list with an initial capacity.
     *
     * @param capacity initial number of tasks the list can hold without resizing
     */
    public TaskList(int capacity) {
        tasks = new ArrayList<>(capacity);
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
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return the task at the given index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return the removed task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword text to search for
     * @return tasks matching the keyword in their list order
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.matches(keyword))
                .toList();
    }

    /**
     * Returns the number of tasks currently in this list.
     *
     * @return current task count
     */
    public int size() {
        return tasks.size();
    }
}
