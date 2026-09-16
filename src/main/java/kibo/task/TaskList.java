package kibo.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Stores and manages the tasks used by Kibo.
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
     * @param tasks tasks to place in the new list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
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
     * Inserts a task at the specified zero-based position.
     *
     * @param index zero-based insertion position.
     * @param task task to insert.
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Returns the task at the specified zero-based position.
     *
     * @param index zero-based task position.
     * @return task at the position.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the specified zero-based position.
     *
     * @param index zero-based task position.
     * @return removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
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
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword text to find in task descriptions.
     * @return matching tasks in their original order.
     */
    public TaskList find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
        return new TaskList(matchingTasks);
    }

    /**
     * Returns deadlines and dated events scheduled on the supplied date.
     *
     * @param date date whose schedule is being viewed.
     * @return matching tasks in their original order.
     */
    public TaskList findScheduledOn(LocalDate date) {
        assert date != null : "A schedule date must not be null";
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.isScheduledOn(date))
                .toList();
        return new TaskList(matchingTasks);
    }

    /**
     * Returns an iterator that does not allow callers to change the list structure.
     *
     * @return read-only task iterator.
     */
    @Override
    public Iterator<Task> iterator() {
        return Collections.unmodifiableList(tasks).iterator();
    }
}
