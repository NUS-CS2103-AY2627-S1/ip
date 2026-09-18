package phin;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the ordered task collection and provides operations that change it.
 */
public class TaskList {
    /**
     * Ordered collection owned by this task list.
     */
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Copies the loaded collection so callers cannot add or remove tasks through it.
     * Task objects themselves are shared, preserving their saved completion status.
     *
     * @param loadedTasks tasks loaded from storage, in their saved order.
     */
    public TaskList(List<Task> loadedTasks) {
        tasks = new ArrayList<>(loadedTasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Appends a task to the end of the list.
     *
     * @param task Task to append.
     */
    public void add(Task task) {
        assert task != null : "Only constructed tasks may be added";
        tasks.add(task);
    }

    /**
     * Finds tasks whose descriptions contain the case-sensitive search phrase.
     * The result preserves order and shares tasks, but cannot change collection membership.
     *
     * @param keyword Nonblank search phrase validated by the parser.
     * @return Unmodifiable matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        assert keyword != null && !keyword.isBlank() : "The parser must validate the search phrase";
        return tasks.stream()
                .filter(task -> task.description.contains(keyword))
                .toList();
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index Zero-based index of the task to remove.
     * @return The removed task.
     * @throws IndexOutOfBoundsException If the index is outside the list.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Updates completion status and returns the affected task for display.
     *
     * @param index Zero-based index already validated by the parser.
     * @param isDone Whether the task should be marked done.
     * @return The updated task.
     * @throws IndexOutOfBoundsException If the index is outside the list.
     */
    public Task setDone(int index, boolean isDone) {
        Task task = tasks.get(index);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        return task;
    }

    /**
     * Checks whether another task has the same type and user-visible details.
     * Completion status is deliberately ignored because it does not make a task unique.
     *
     * @param candidate Task whose details should be compared.
     * @return True if an equivalent task is already stored.
     */
    public boolean containsEquivalent(Task candidate) {
        assert candidate != null : "Only a constructed task may be compared";
        return tasks.stream().anyMatch(task -> hasSameDetails(task, candidate));
    }

    private static boolean hasSameDetails(Task first, Task second) {
        if (first.getClass() != second.getClass() || !first.description.equals(second.description)) {
            return false;
        }
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return firstDeadline.by.equals(secondDeadline.by);
        }
        if (first instanceof Event firstEvent && second instanceof Event secondEvent) {
            return firstEvent.from.equals(secondEvent.from) && firstEvent.to.equals(secondEvent.to);
        }
        return true;
    }

    /**
     * Replaces one task with a validated updated copy.
     *
     * @param index Zero-based index already validated by the parser.
     * @param updatedTask Updated task to store at that index.
     * @return The stored updated task.
     */
    public Task update(int index, Task updatedTask) {
        assert updatedTask != null : "Only a constructed task may replace an existing task";
        tasks.set(index, updatedTask);
        return updatedTask;
    }

    /**
     * Returns an unmodifiable snapshot of the current task ordering.
     * Task objects are shared; collection edits are prevented, but task edits remain visible.
     *
     * @return An unmodifiable list containing the same task objects.
     * @throws NullPointerException If the list contains a null task.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
