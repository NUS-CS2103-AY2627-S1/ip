package della.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages an ordered collection of tasks.
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
     * Creates a task list containing the specified tasks.
     *
     * @param tasks Tasks used to initialise the task list.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task collection must not be null";
        assert tasks.stream().allMatch(task -> task != null) : "Initial task collection must not contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of this task list.
     *
     * @param task Task to add.
     * @throws IllegalArgumentException If task to be added is a duplicate.
     */
    public void add(Task task) throws IllegalArgumentException {
        assert task != null : "Task list must not contain null tasks";
        if (tasks.contains(task)) {
            throw new IllegalArgumentException("Cannot add duplicate task!");
        }
        tasks.add(task);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index Zero-based position of the task.
     * @return Task at the specified index.
     * @throws IndexOutOfBoundsException If the index is outside this task list.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Deletes and returns the task at the specified zero-based index.
     *
     * @param index Zero-based position of the task.
     * @return Deleted task.
     * @throws IndexOutOfBoundsException If the index is outside this task list.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks and returns the task at the specified zero-based index.
     *
     * @param index Zero-based position of the task.
     * @return Task after it is marked as complete.
     * @throws IndexOutOfBoundsException If the index is outside this task list.
     */
    public Task mark(int index) {
        Task task = tasks.get(index);
        task.mark();
        return task;
    }

    /**
     * Marks and returns the task at the specified zero-based index as incomplete.
     *
     * @param index Zero-based position of the task.
     * @return Task after it is marked as incomplete.
     * @throws IndexOutOfBoundsException If the index is outside this task list.
     */
    public Task unmark(int index) {
        Task task = tasks.get(index);
        task.unmark();
        return task;
    }

    /**
     * Returns the number of tasks in this task list.
     *
     * @return Number of tasks in this task list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a copy of the tasks in this task list.
     *
     * @return Copy of the tasks in their current order.
     */
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(List.copyOf(tasks));
    }
}
