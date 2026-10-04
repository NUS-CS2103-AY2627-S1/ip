package shrek.task;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Contains the task list and operations to add, delete, and access tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates a new, empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list wrapping an already-loaded list of tasks.
     *
     * @param loadedTasks the tasks loaded from disk, or elsewhere.
     */
    public TaskList(ArrayList<Task> loadedTasks) {
        // Storage always returns a list, even when the data file is missing.
        assert loadedTasks != null : "A task list cannot wrap a null list.";
        this.tasks = loadedTasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        // Null entries would break display, search, and persistence invariants.
        assert task != null : "A task list cannot contain a null task.";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index the zero-based index of the task to remove.
     * @return the removed task.
     */
    public Task remove(int index) {
        // Callers convert the user's one-based number into this valid zero-based index.
        assert index >= 0 && index < tasks.size() : "Task index must refer to an existing task.";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index the zero-based index of the task.
     * @return the task at that index.
     */
    public Task get(int index) {
        // Callers should validate indices before accessing the list.
        assert index >= 0 && index < tasks.size() : "Task index must refer to an existing task.";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list of all tasks.
     *
     * @return the full list of tasks.
     */
    public ArrayList<Task> getAll() {
        return tasks;
    }

    /**
     * Returns a list of tasks whose description or tags contain the given keyword.
     * The match is case-insensitive and substring-based.
     *
     * @param keyword the search term to match against task descriptions and tags.
     * @return a list of tasks matching the keyword.
     */
    public ArrayList<Task> find(String keyword) {
        assert keyword != null : "Search keywords must not be null.";
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> {
                    assert task != null : "The task list invariant forbids null tasks.";
                    String lowerDescription = task.getDescription().toLowerCase();
                    String lowerTags = String.join(" ", task.getTags()).toLowerCase();
                    return lowerDescription.contains(lowerKeyword) || lowerTags.contains(lowerKeyword);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
