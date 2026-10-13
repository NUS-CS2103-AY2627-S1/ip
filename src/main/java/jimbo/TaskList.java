package jimbo;

import jimbo.task.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a list of tasks.
 */
public class TaskList {
    private List<Task> tasks;

    /**
     * Constructs an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList from a raw Java List of tasks.
     *
     * @param tasks List of tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "List of tasks given to initialise a TaskList should not be null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        assert tasks != null : "tasks is supposed to have been initialised";
        tasks.add(task);
    }

    /**
     * Deletes a task from the list based on the index provided.
     *
     * @param index The index of the task to delete.
     */
    public void delete(int index) {
        assert tasks != null : "tasks is supposed to have been initialised";
        tasks.remove(index);
    }

    /**
     * Gets the task in the list with the specified index.
     *
     * @param index The index of the task.
     * @return The task.
     */
    public Task get(int index) {
        assert tasks != null : "tasks is supposed to have been initialised";
        return tasks.get(index);
    }

    /**
     * Calculates how many total tasks are in the list.
     *
     * @return The number of tasks in the list.
     */
    public int size() {
        assert tasks != null : "tasks is supposed to have been initialised";
        return tasks.size();
    }

    /**
     * Returns whether the list is empty
     *
     * @return Whether the list is empty.
     */
    public boolean isEmpty() {
        assert tasks != null : "tasks is supposed to have been initialised";
        return tasks.isEmpty();
    }

    /**
     * Returns the list of tasks formatted as a String.
     *
     * @return The list tasks as a String.
     */
    public String listAll() {
        assert tasks != null : "tasks is supposed to have been initialised";
        StringBuilder sb = new StringBuilder();
        int counter = 1;
        for (Task task : tasks) {
            sb.append(counter + ". " + task);
            sb.append('\n');
            counter++;
        }
        assert counter - 1 == size() : "Number of tasks listed should match number of tasks stored";
        return sb.toString();
    }

    /**
     * Returns the raw internal List representation of the tasks.
     *
     * @return Raw List of tasks.
     */
    public List<Task> getInternalList() {
        return tasks;
    }
}
