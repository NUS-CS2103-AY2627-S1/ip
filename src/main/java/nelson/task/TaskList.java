package nelson.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Stores Nelson's tasks and provides operations on the task collection.
 */
public class TaskList implements Iterable<Task> {
    /** The tasks currently known to Nelson. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the tasks from the given source.
     * @param source tasks to copy into this list
     */
    public TaskList(Iterable<Task> source) {
        this();
        for (Task task : source) {
            add(task);
        }
    }

    /**
     * Adds a task to the end of this list.
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     * @param index zero-based index of the task to remove
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given zero-based index.
     * @param index zero-based index of the requested task
     * @return the task at {@code index}
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in this list.
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /** Removes all tasks from this list. */
    public void clear() {
        tasks.clear();
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword text to search for.
     * @return matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /** Allows storage code to iterate over tasks without owning the collection. */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
