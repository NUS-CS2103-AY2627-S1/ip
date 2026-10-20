package yapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Owns the task collection and operations on tasks, independently of input and storage.
 */
public class TaskList {
    /** Mutable task collection in display and storage order. */
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Copies loaded tasks so callers cannot add or remove entries through the original list.
     *
     * @param tasks Initial tasks; the task objects themselves remain shared.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to append.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at a validated zero-based index.
     *
     * @param index Zero-based task index.
     * @return Removed task.
     * @throws IndexOutOfBoundsException If the index is outside the list.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks a task at a validated zero-based index as completed.
     *
     * @param index Zero-based task index.
     * @return Task after marking it completed.
     * @throws IndexOutOfBoundsException If the index is outside the list.
     */
    public Task mark(int index) {
        Task task = get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task at a validated zero-based index as incomplete.
     *
     * @param index Zero-based task index.
     * @return Task after marking it incomplete.
     * @throws IndexOutOfBoundsException If the index is outside the list.
     */
    public Task unmark(int index) {
        Task task = get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns a task at a validated zero-based index.
     *
     * @param index Zero-based task index.
     * @return Task at that index.
     * @throws IndexOutOfBoundsException If the index is outside the list.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks.
     *
     * @return Current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a structurally immutable snapshot for display or saving; task objects are shared.
     *
     * @return Unmodifiable copy of the list containing the same mutable task objects.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Finds matching descriptions without changing the task list.
     *
     * @param keyword Nonempty search text validated by the parser.
     * @return Matching zero-based indices in their original order.
     */
    public List<Integer> indicesMatching(String keyword) {
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).containsKeyword(keyword))
                .boxed()
                .toList();
    }

    /**
     * Finds zero-based indices of scheduled tasks, preserving their original list numbers.
     *
     * @param date Calendar date to match.
     * @return Matching indices in list order; empty when no tasks occur on the date.
     */
    public List<Integer> indicesOn(LocalDate date) {
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).occursOn(date))
                .boxed()
                .toList();
    }
}
