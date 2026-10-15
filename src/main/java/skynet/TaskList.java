package skynet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a collection of tasks and provides operations to modify/access it.
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
     * @param tasks the initial list of tasks
     */
    public TaskList(List<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("Task list cannot be null.");
        }
        if (tasks.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Task list cannot contain null tasks.");
        }
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the task list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        tasks.add(task);
    }

    /**
     * Retrieves a task using a given index.
     *
     * @param index the position of the task in the ArrayList
     * @return The task at the specified index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Deletes a task in the task list.
     *
     * @param index the position of the task in the ArrayList
     * @return The task at the specified index
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Gets the size of the TaskList.
     *
     * @return Number of tasks in the TaskList
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Gets the list of tasks.
     *
     * @return The List of Tasks
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Finds the list of tasks matching the keyword.
     *
     * @return The List of Tasks
     */
    public List<Task> find(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Search keyword cannot be empty.");
        }
        List<Task> matches = new ArrayList<>();
        String lowercaseKeyword = keyword.toLowerCase(Locale.ROOT);

        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(lowercaseKeyword)) {
                matches.add(task);
            }
        }

        return matches;
    }
}
