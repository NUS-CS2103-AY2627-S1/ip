package johnny.task;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Manages an ordered list of tasks with operations to add, delete,
 * and retrieve tasks.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list pre-populated with the given tasks. */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void add(Task task) {
        tasks.add(task);
    }

    /** Inserts a task at the given zero-based index. */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    /**
     * Returns a new list containing tasks whose descriptions contain the keyword.
     * The search is case-insensitive.
     */
    public ArrayList<Task> find(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        ArrayList<Task> matches = tasks.stream()
                .filter(task -> task.toString().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
        assert matches.size() <= tasks.size() && tasks.containsAll(matches)
                : "Search results must be a subset of the task list";
        return matches;
    }

    /** Returns the underlying list for serialization by Storage. */
    public ArrayList<Task> getAll() {
        return tasks;
    }
}
