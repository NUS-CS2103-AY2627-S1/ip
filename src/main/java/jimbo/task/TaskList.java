package jimbo.task;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents the in-memory list of tasks. Wraps an {@link ArrayList} of
 * {@link Task}s and exposes the operations callers need (add, delete,
 * retrieve, count) so they don't have to work with the raw collection
 * directly.
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
     * Creates a task list backed by the given tasks, e.g. ones loaded from
     * the save file.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given 0-based index.
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size()
                : "index should already be validated by the caller (e.g. via Parser.parseTaskIndex)";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given 0-based index.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "index should already be validated by the caller (e.g. via Parser.parseTaskIndex)";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list of tasks, e.g. for {@link Storage} to
     * persist to disk.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns the tasks whose description contains {@code keyword}
     * (case-insensitive), in their original order.
     */
    public ArrayList<Task> find(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
