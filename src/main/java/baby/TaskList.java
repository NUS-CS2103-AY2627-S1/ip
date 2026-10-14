package baby;

import java.util.ArrayList;

/**
 * Stores and provides simple operations for the task list.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Creates a task list using tasks that were already loaded.
     *
     * @param tasks The initial tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Gets a task by its zero-based index.
     *
     * @param index The zero-based index.
     * @return The task at the index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes a task by its zero-based index.
     *
     * @param index The zero-based index.
     * @return The removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
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
     * Returns the underlying task list for storage and display.
     *
     * @return The underlying list of tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
