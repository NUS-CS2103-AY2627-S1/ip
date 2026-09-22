package wiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents a list of tasks and provides operations to manipulate them.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList initialized with the given list of tasks.
     *
     * @param tasks The ArrayList of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Initial task list cannot be null";
        this.tasks = tasks;
    }

    /**
     * Constructs a TaskList initialized with varargs tasks.
     *
     * @param tasks The array/varargs of tasks.
     */
    public TaskList(Task... tasks) {
        assert tasks != null : "Tasks varargs cannot be null";
        this.tasks = new ArrayList<>(Arrays.asList(tasks));
    }

    /**
     * Adds one or more tasks to the task list.
     *
     * @param tasks The tasks to add.
     */
    public void add(Task... tasks) {
        assert tasks != null : "Tasks to add cannot be null";
        for (Task task : tasks) {
            assert task != null : "Cannot add a null task to TaskList";
            this.tasks.add(task);
        }
    }

    /**
     * Deletes and returns the task at the specified index.
     *
     * @param index The zero-based index of the task to delete.
     * @return The removed task.
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "Index out of bounds for deletion";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index The zero-based index of the task.
     * @return The task at the given index.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Index out of bounds for retrieval";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The size of the task list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return The ArrayList of tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Checks if a duplicate task already exists in the list.
     *
     * @param task The task to check.
     * @return True if an identical task is found, false otherwise.
     */
    public boolean hasDuplicate(Task task) {
        assert task != null : "Task to check cannot be null";
        return tasks.stream().anyMatch(t -> t.equals(task));
    }

    /**
     * Finds tasks that contain the specified keyword in their string representation.
     *
     * @param keyword The keyword to search for.
     * @return A list of tasks matching the keyword.
     */
    public ArrayList<Task> find(String keyword) {
        assert keyword != null : "Search keyword cannot be null";
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.toString().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}