package luna.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the tasks managed by Luna and provides operations for accessing them.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the tasks in the given list.
     *
     * @param tasks Tasks to include in the new task list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to this list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index Zero-based index of the task to remove.
     * @return Removed task.
     * @throws IndexOutOfBoundsException If the index is outside the list.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index Zero-based index of the task to retrieve.
     * @return Task at the specified index.
     * @throws IndexOutOfBoundsException If the index is outside the list.
     */
    public Task get(int index) {
        return tasks.get(index);
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
     * Returns an unmodifiable copy of the tasks in this list.
     *
     * @return Unmodifiable task list.
     */
    public List<Task> getUnmodifiableList() {
        return List.copyOf(tasks);
    }
}
