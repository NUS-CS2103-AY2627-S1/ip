package nutrisoy.task;

import java.util.ArrayList;

/**
 * Maintains the application's ordered collection of tasks.
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
     * Creates a task list backed by the supplied tasks.
     *
     * @param tasks tasks to include in the list
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Task list must not be null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "Task being added must not be null";
        tasks.add(task);
    }

    /**
     * Returns whether this list already contains a task with the same identifying details.
     *
     * @param candidate task to compare against the list
     * @return {@code true} if an equivalent task exists
     */
    public boolean containsSameTask(Task candidate) {
        assert candidate != null : "Candidate task must not be null";
        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index zero-based index of the task to remove
     * @return removed task
     * @throws IndexOutOfBoundsException if the index is outside this list's range
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "Task removal index must be within bounds";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index zero-based index of the task to retrieve
     * @return task at the specified index
     * @throws IndexOutOfBoundsException if the index is outside this list's range
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task access index must be within bounds";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Indicates whether this list contains no tasks.
     *
     * @return {@code true} if this list is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks in this list.
     *
     * @return list of tasks backing this task list
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
