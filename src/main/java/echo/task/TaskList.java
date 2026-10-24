package echo.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps the in-memory list of tasks and the membership operations Echo
 * performs on it (add/remove/get/size). Marking a task done/not-done stays
 * that Task's own responsibility (Task#markAsDone/markAsNotDone) rather
 * than living here, since that's a property of one task, not the list.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Wraps the given list of tasks.
     *
     * @param tasks The initial tasks (e.g. freshly loaded from Storage).
     */
    public TaskList(List<Task> tasks) {
        // Storage.load() always returns a list (an empty one on IO failure,
        // never null), and that's the only real call site, so a null here
        // would mean a caller broke that contract, not a runtime condition.
        assert tasks != null : "tasks list should not be null";
        this.tasks = tasks;
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns whether the given task duplicates a task already in the
     * list, per {@link Task#isDuplicateOf(Task)}.
     */
    public boolean containsDuplicateOf(Task task) {
        return tasks.stream().anyMatch(existing -> existing.isDuplicateOf(task));
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task remove(int index) {
        // Callers reach this via Parser.parseTaskIndex, which already
        // validates the index against tasks.size() and raises an
        // EchoException otherwise, so an out-of-range index here would
        // signal a bug in the caller, not bad user input.
        assert index >= 0 && index < tasks.size() : "index should already be validated by the caller";
        return tasks.remove(index);
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "index should already be validated by the caller";
        return tasks.get(index);
    }

    /** Returns the number of tasks currently in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Removes every task from the list and returns them, in their
     * previous order, so a caller (e.g. an archive command) can do
     * something with them before the list becomes empty.
     */
    public List<Task> clearAll() {
        List<Task> removed = new ArrayList<>(tasks);
        tasks.clear();
        return removed;
    }

    /**
     * Returns the underlying list, for callers (Ui rendering, Storage
     * saving) that need to read or persist every task.
     */
    public List<Task> getAll() {
        return tasks;
    }
}
