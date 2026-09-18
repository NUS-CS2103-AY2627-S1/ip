package mochi.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Owns the collection of tasks and provides task-list operations.
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
     * @param tasks tasks to place in the list
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "The initial task collection must exist";
        assert tasks.stream().allMatch(task -> task != null)
                : "The initial task collection must not contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "Only non-null tasks may be added to the list";
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return selected task
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Copies the list and its tasks, isolating changes until storage succeeds.
     *
     * @return independent task list
     */
    public TaskList copy() {
        return new TaskList(tasks.stream().map(Task::copy).toList());
    }

    /**
     * Returns a new task list containing tasks whose descriptions match the keyword.
     *
     * @param keyword text to find, matched without regard to letter case
     * @return matching tasks in their original order
     */
    public TaskList find(String keyword) {
        return new TaskList(tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .toList());
    }

    /**
     * Sorts tasks alphabetically by description, ignoring case.
     * Tasks with equal descriptions retain their relative order.
     */
    public void sortByDescription() {
        tasks.sort(Comparator.comparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER));
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
