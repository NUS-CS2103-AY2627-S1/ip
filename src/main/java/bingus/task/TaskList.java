package bingus.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents a collection of tasks.
 */
public class TaskList {

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Tasks initially contained in the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Adds a task at the specified zero-based position.
     *
     * @param index Zero-based position at which to add the task.
     * @param task Task to add.
     */
    public void add(int index, Task task) {
        this.tasks.add(index, task);
    }

    /**
     * Returns the task at the specified position.
     *
     * @param index Zero-based position of the task.
     * @return Task at the specified position.
     */
    public Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Removes and returns the task at the specified position.
     *
     * @param index Zero-based position of the task.
     * @return Removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot of the tasks in this list.
     *
     * @return Tasks in their current order.
     */
    public List<Task> getTasksSnapshot() {
        return List.copyOf(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword, ignoring case.
     *
     * @param keyword keyword to search for.
     * @return matching tasks in their current order.
     */
    public List<Task> findByKeyword(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);

        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
    }

    /**
     * Replaces the task at the specified zero-based position.
     *
     * @param index zero-based position of the task to replace.
     * @param replacement task to store at the specified position.
     * @return task previously stored at the specified position.
     */
    public Task replace(int index, Task replacement) {
        return tasks.set(index, replacement);
    }

}
