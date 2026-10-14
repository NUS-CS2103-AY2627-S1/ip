package milo.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns Milo's ordered collection of tasks and provides operations that mutate it.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in their existing order.
     *
     * @param tasks tasks with which to initialize the list.
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
     * Restores a task at a specific position in the list.
     *
     * @param index zero-based position at which to add the task.
     * @param task task to add.
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at a specific position.
     *
     * @param index zero-based position of the task to remove.
     * @return removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at a specific position.
     *
     * @param index zero-based position of the task.
     * @return selected task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Marks and returns the task at a specific position.
     *
     * @param index zero-based position of the task.
     * @return marked task.
     */
    public Task mark(int index) {
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks and returns the task at a specific position.
     *
     * @param index zero-based position of the task.
     * @return unmarked task.
     */
    public Task unmark(int index) {
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns tasks whose descriptions contain the keyword, preserving their order.
     *
     * @param keyword non-empty keyword for which to search.
     * @return new task list containing the matching tasks.
     */
    public TaskList find(String keyword) {
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.hasKeyword(keyword))
                .toList();
        return new TaskList(matchingTasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an immutable snapshot of the tasks in their current order.
     *
     * @return immutable snapshot of the tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /** Removes every task from the list. */
    public void clear() {
        tasks.clear();
    }
}
