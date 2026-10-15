package lily.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns Lily's ordered collection of tasks and provides its basic task
 * operations.
 *
 * <p>
 * The class deliberately does not print messages. Command-specific feedback
 * belongs to
 * the user-interface layer, while this class focuses only on changing or
 * retrieving tasks.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list populated with the supplied saved tasks. */
    public TaskList(List<Task> tasks) {
        // Storage.load() returns a concrete list, so this constructor never owns null.
        assert tasks != null : "A task list requires an initial collection";
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        // All task creation paths construct a real Task before adding it to the list.
        assert task != null : "A task list cannot contain null tasks";
        tasks.add(task);
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int index) {
        // Lily validates user-supplied task numbers before delegating to TaskList.
        assert containsIndex(index) : "Task index must identify an existing task";
        return tasks.get(index);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task remove(int index) {
        // Lily validates user-supplied task numbers before delegating to TaskList.
        assert containsIndex(index) : "Task index must identify an existing task";
        return tasks.remove(index);
    }

    /** Marks the task at the given zero-based index as done. */
    public void mark(int index) {
        // Lily validates user-supplied task numbers before delegating to TaskList.
        assert containsIndex(index) : "Task index must identify an existing task";
        tasks.get(index).markAsDone();
    }

    /** Marks the task at the given zero-based index as not done. */
    public void unmark(int index) {
        // Lily validates user-supplied task numbers before delegating to TaskList.
        assert containsIndex(index) : "Task index must identify an existing task";
        tasks.get(index).markAsUndone();
    }

    /** Returns whether the index identifies an existing task. */
    public boolean containsIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /** Returns the number of tasks currently held. */
    public int size() {
        return tasks.size();
    }

    /** Returns a snapshot suitable for passing to persistence code. */
    public List<Task> toList() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns all tasks whose descriptions contain the given keyword
     * (case-insensitive).
     */
    public List<Task> findTasks(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();

        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerCaseKeyword))
                .toList();
    }
}
