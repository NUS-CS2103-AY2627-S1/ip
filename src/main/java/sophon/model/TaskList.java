package sophon.model;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Stores the tasks currently tracked by Sophon.
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
     * Creates a task list containing the given tasks.
     *
     * @param tasks tasks to store.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Backing task list must not be null";
        assert !tasks.contains(null) : "Task list must not contain null tasks";

        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based task index.
     * @return task at the given index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based task index.
     * @return removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns tasks whose descriptions match the given keyword exactly or fuzzily.
     *
     * @param keyword keyword to search for.
     * @return tasks with matching descriptions.
     */
    public TaskList find(String keyword) {
        ArrayList<Task> matchingTasks = tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));

        return new TaskList(matchingTasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks whether the given zero-based index points to an existing task.
     *
     * @param index zero-based task index.
     * @return true if a task exists at the index.
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}
