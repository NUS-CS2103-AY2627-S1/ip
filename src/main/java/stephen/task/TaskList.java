package stephen.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Stores tasks and provides the operations that update or retrieve them. */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a task list containing the tasks loaded from storage.
     *
     * @param initialTasks tasks with which to initialize the list
     */
    public TaskList(List<Task> initialTasks) {
        assert initialTasks != null : "The initial task collection must not be null";
        assert initialTasks.stream().allMatch(Objects::nonNull)
                : "The initial task collection must not contain null tasks";
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "A task list must not contain a null task";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based index of the task to remove
     * @return removed task
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based index of the task to retrieve
     * @return task at the index
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Marks the task at the given zero-based index as done.
     *
     * @param index zero-based index of the task to mark
     * @return marked task
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public Task mark(int index) {
        Task task = get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given zero-based index as not done.
     *
     * @param index zero-based index of the task to unmark
     * @return unmarked task
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public Task unmark(int index) {
        Task task = get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns tasks whose descriptions contain the search phrase.
     *
     * @param keyword search phrase to match
     * @return matching tasks in their original list order
     */
    public List<Task> find(String keyword) {
        return tasks.stream().filter(task -> task.matches(keyword)).toList();
    }

    /** Sorts tasks alphabetically by description, ignoring letter case. */
    public void sortByDescription() {
        tasks.sort(Comparator.comparing(Task::getDescription,
                String.CASE_INSENSITIVE_ORDER));
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
     * Returns a read-only snapshot suitable for display or persistence.
     *
     * @return immutable copy of the tasks in list order
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
