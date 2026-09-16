package mouse.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mouse.MouseException;

/**
 * Stores a dynamic list of tasks and updates their status.
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
     * Creates a task list that already holds {@code tasks}.
     * Used when loading from disk so duplicate lines are kept as stored.
     *
     * @param tasks Tasks to copy.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns how many tasks are stored.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable view of the stored tasks.
     *
     * @return Tasks in list order.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns the task at the given 0-based index.
     *
     * @param index Zero-based index.
     * @return Task at that index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds {@code task} and returns it.
     *
     * @param task Task to add.
     * @return The same task.
     * @throws MouseException If an equivalent crumb is already in the list.
     */
    public Task add(Task task) throws MouseException {
        for (Task existing : tasks) {
            if (existing.isDuplicateOf(task)) {
                throw new MouseException("That crumb is already in the stash GRR");
            }
        }
        tasks.add(task);
        return task;
    }

    /**
     * Marks the task at the 0-based {@code index} as done.
     *
     * @param index Zero-based index.
     * @return The marked task.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public Task mark(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException();
        }
        assert index >= 0 && index < tasks.size() : "Mark index should be in range after the bounds check";
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the 0-based {@code index} as not done.
     *
     * @param index Zero-based index.
     * @return The unmarked task.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public Task unmark(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException();
        }
        assert index >= 0 && index < tasks.size() : "Unmark index should be in range after the bounds check";
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Removes and returns the task at the 0-based {@code index}.
     *
     * @param index Zero-based index.
     * @return The removed task.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public Task delete(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException();
        }
        assert index >= 0 && index < tasks.size() : "Delete index should be in range after the bounds check";
        return tasks.remove(index);
    }

    /**
     * Sets the priority of the task at the 0-based {@code index}.
     *
     * @param index Zero-based index.
     * @param priority Priority to assign.
     * @return The updated task.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public Task setPriority(int index, Priority priority) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException();
        }
        assert index >= 0 && index < tasks.size() : "Priority index should be in range after the bounds check";
        Task task = tasks.get(index);
        task.setPriority(priority);
        return task;
    }

    /**
     * Returns tasks whose description contains {@code keyword} (case-insensitive).
     *
     * @param keyword Text to look for in descriptions.
     * @return A new list of matching tasks.
     */
    public TaskList find(String keyword) {
        String needle = keyword.toLowerCase();
        List<Task> matches = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(needle))
                .toList();
        return new TaskList(matches);
    }
}
