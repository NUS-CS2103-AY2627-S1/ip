package bo.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the ordered collection of tasks used by Bo.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing saved tasks.
     *
     * @param tasks The tasks to place in this list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at a zero-based list position.
     *
     * @param index The zero-based position at which to insert the task.
     * @param task The task to insert.
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Removes and returns a task at a zero-based list position.
     *
     * @param index The zero-based position of the task to remove.
     * @return The removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns a task at a zero-based list position.
     *
     * @param index The zero-based position of the task.
     * @return The requested task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks whether a one-based task number refers to a task in this list.
     *
     * @param taskNumber The task number shown to the user.
     * @return True if the task number is valid.
     */
    public boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }

    /**
     * Returns a read-only snapshot of the current tasks.
     *
     * @return The current tasks in list order.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
