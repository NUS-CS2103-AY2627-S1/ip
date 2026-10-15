package coco;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps the in-memory list of tasks and the operations to add/remove/access them.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by the given tasks, e.g. ones just loaded
     * from disk.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index Zero-based index of the task to remove.
     * @return The removed task.
     */
    public Task remove(int index) {
        // Coco always range-checks index via Parser.parseIndex before calling
        // this; documents that this method trusts its caller rather than
        // re-validating.
        assert index >= 0 && index < tasks.size() : "index should already be validated by the caller";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Zero-based index of the task.
     * @return The task at that index.
     */
    public Task get(int index) {
        // Same trust-the-caller assumption as remove(int): index is expected
        // to already be validated by Parser.parseIndex.
        assert index >= 0 && index < tasks.size() : "index should already be validated by the caller";
        return tasks.get(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list, e.g. so Storage can save it to disk.
     */
    public List<Task> asList() {
        return tasks;
    }

    /**
     * Returns the tasks whose description contains the given keyword
     * (case-insensitive).
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks, in original list order.
     */
    public List<Task> find(String keyword) {
        String needle = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(needle))
                .toList();
    }
}
