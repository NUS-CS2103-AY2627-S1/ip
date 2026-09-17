package orbit;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the user's tasks and exposes task-list operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must exist";
        assert tasks.stream().noneMatch(task -> task == null) : "Initial tasks must not contain null";
        this.tasks = new ArrayList<>(tasks);
    }

    /** @param task Task to append. */
    public void add(Task task) {
        assert task != null : "Only constructed tasks may be added";
        tasks.add(task);
    }

    /**
     * Checks for an existing task with matching details, including completed tasks.
     *
     * @param task Proposed task.
     * @return Whether a duplicate is already in the list.
     */
    public boolean containsDuplicate(Task task) {
        return tasks.stream().anyMatch(existing -> existing.isDuplicateOf(task));
    }

    /**
     * Returns the task at an index.
     *
     * @param index Zero-based task index.
     * @return Task at the index.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be validated before lookup";
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at an index.
     *
     * @param index Zero-based task index.
     * @return Removed task.
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be validated before removal";
        return tasks.remove(index);
    }

    /** @return Number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** @return Immutable snapshot of the tasks. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Finds tasks whose descriptions contain a keyword, ignoring case.
     *
     * @param keyword Search keyword.
     * @return Matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(normalizedKeyword))
                .toList();
    }
}
