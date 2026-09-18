package kopi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Owns the task collection and its list operations. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "A task list cannot be created from null";
        assert tasks.stream().noneMatch(task -> task == null)
                : "A task list cannot contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        assert task != null : "A task list cannot contain null tasks";
        tasks.add(task);
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int index) {
        assert isValidIndex(index) : "Task index must be within the list";
        return tasks.get(index);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task delete(int index) {
        assert isValidIndex(index) : "Task index must be within the list";
        return tasks.remove(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns an immutable snapshot of all tasks. */
    public List<Task> getAll() {
        return List.copyOf(tasks);
    }

    /** Returns whether the list contains a task with the same details. */
    public boolean hasDuplicateOf(Task candidate) {
        assert candidate != null : "A duplicate candidate cannot be null";
        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring case. */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}
