package benji;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of tasks managed by BENJI.
 *
 * <p>Provides operations for adding, retrieving, deleting, and counting tasks.</p>
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
     * Creates a task list containing the given tasks.
     *
     * @param tasks to assign
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the task list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {

        assert task != null : "A task list should never contain a null task";
        this.tasks.add(task);
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index the index of the task.
     * @return the task at the specified index.
     */
    public Task get(int index) {

        assert index >= 0 && index < tasks.size() : "index should be a valid index";
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index the index of the task to remove
     * @return the removed task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return the list containing all tasks
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
