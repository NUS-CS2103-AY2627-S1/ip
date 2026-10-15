package ip.collection;

import java.util.ArrayList;
import java.util.List;

import ip.model.Task;

/**
 * Stores and provides operations for the user's tasks.
 */
public class TaskList {

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by the supplied tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Task list storage must not be null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of this list.
     */
    public void add(Task task) {
        assert task != null : "A task list must not contain null tasks";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the zero-based index.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the zero-based index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in this list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     */
    public List<Task> find(String keyword) {
        assert keyword != null : "Search keywords must not be null";
        String normalizedKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(normalizedKeyword))
                .toList();
    }
}
