package epi.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Maintains the ordered collection of tasks used by the application. */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Returns an independent snapshot so a failed save cannot alter the live task list.
     *
     * @return A new list of copied tasks in the same order, preserving their types and completion states.
     */
    public TaskList copy() {
        TaskList snapshot = new TaskList();
        for (Task task : tasks) {
            snapshot.add(task.copy());
        }
        return snapshot;
    }

    /**
     * Adds a task to the end of the list without copying it.
     *
     * @param task Non-null task to append.
     * @throws AssertionError If the task is null and assertions are enabled.
     */
    public void add(Task task) {
        assert task != null : "A task list must not contain null tasks";
        tasks.add(task);
        assert tasks.get(tasks.size() - 1) == task : "Added task must be stored at the end of the list";
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index Index of an existing task, from zero to {@code size() - 1}.
     * @return The stored task, not a copy.
     * @throws AssertionError If the index is out of range and assertions are enabled.
     * @throws IndexOutOfBoundsException If the index is out of range and assertions are disabled.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must refer to an existing task";
        return tasks.get(index);
    }

    /**
     * Removes the task at the specified zero-based index, shifting later tasks one position left.
     *
     * @param index Index of an existing task, from zero to {@code size() - 1}.
     * @throws AssertionError If the index is out of range and assertions are enabled.
     * @throws IndexOutOfBoundsException If the index is out of range and assertions are disabled.
     */
    public void delete(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must refer to an existing task";
        tasks.remove(index);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return The task count, including both completed and incomplete tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case using the default locale.
     *
     * @param keyword Non-null literal substring to find; an empty string matches every task.
     * @return A new list of matching task references in insertion order, or an empty list if none match.
     */
    public List<Task> find(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.description.toLowerCase().contains(lowerKeyword)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /**
     * Returns tasks in earliest-first date order without changing the original list.
     * Undated tasks come last; tasks with equal dates retain their insertion order.
     *
     * @return A new list containing the existing task objects in date order.
     */
    public List<Task> getSortedByDate() {
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(Comparator.comparing(Task::getSortDate,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return sortedTasks;
    }

    /**
     * Returns the zero-based index of a task in the list.
     *
     * @param task Task object to locate in the full list.
     * @return The first matching index, or {@code -1} if the task is absent.
     */
    public int getIndex(Task task) {
        return tasks.indexOf(task);
    }

    /**
     * Returns an iterator over tasks in insertion order.
     *
     * @return An iterator backed by this list, not an independent snapshot.
     */
    @Override
    public java.util.Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
