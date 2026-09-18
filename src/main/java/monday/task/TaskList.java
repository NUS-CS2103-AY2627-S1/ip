package monday.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Stores and provides operations on Monday's tasks.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to include in this task list.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of this task list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Inserts a task at the specified zero-based index.
     *
     * @param index zero-based index at which to insert the task.
     * @param task task to insert.
     */
    public void add(int index, Task task) {
        this.tasks.add(index, task);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index zero-based index of the task.
     * @return the task at the given index.
     */
    public Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index zero-based index of the task.
     * @return the removed task.
     */
    public Task remove(int index) {
        return this.tasks.remove(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return number of tasks.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns an iterator over the tasks in this list.
     *
     * @return iterator over the tasks.
     */
    @Override
    public Iterator<Task> iterator() {
        return this.tasks.iterator();
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword keyword to search for
     * @return task list containing the matching tasks
     */
    public TaskList find(String keyword) {
        return new TaskList(this.tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(keyword.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toCollection(ArrayList::new)));
    }
}
