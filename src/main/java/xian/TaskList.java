package xian;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Represents a list of tasks.
 * Provides operations to add, delete, retrieve, and iterate over tasks.
 */
public class TaskList implements Iterable<Task> {

    private final List<Task> tasks;

    /**
     * Creates a new, empty TaskList.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Returns the number of tasks currently in this list.
     *
     * @return the number of tasks.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index The zero-based index of the task to retrieve.
     * @return the task at the given index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Deletes and returns the task at the specified one-based index.
     *
     * @param index The one-based index of the task to delete.
     * @return the task that was removed.
     */
    public Task delete(int index) {
        return tasks.remove(index - 1);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        assert task != null : "Task added to the list must not be null";
        tasks.add(task);
    }

    /**
     * Returns the tasks whose descriptions contain the given keyword,
     * ignoring differences in letter case.
     *
     * @param keyword The keyword to search for.
     * @return A task list containing the matching tasks.
     */
    public TaskList find(String keyword) {
        TaskList matchingTasks = new TaskList();
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);

        tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(lowerCaseKeyword))
                .forEach(matchingTasks::add);

        return matchingTasks;
    }

    /**
     * Returns an iterator over the tasks in this list.
     *
     * @return an iterator over the tasks.
     */
    @Override
    public Iterator<Task> iterator() {
        return this.tasks.iterator();
    }

}
