package gepit;

import java.util.ArrayList;
import java.util.List;

import gepit.task.Task;

/**
 * Manages the user's collection of tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the specified tasks.
     *
     * @param tasks Tasks to add to the new task list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index Index of the task.
     * @return Task at the specified index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the specified index.
     * @param index Index of the task to remove.
     * @return Removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns all tasks in this list.
     *
     * @return Copy of the task collection.
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns all tasks that contain the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return Tasks containing the keyword.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase()
                        .contains(normalizedKeyword))
                .toList();
    }
}
