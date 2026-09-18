package littledaisy.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Owns the application's ordered collection of tasks. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks initial tasks in list order
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based task position
     * @return task at the specified position
     */
    public Task get(int index) {
        assert isValidIndex(index) : "task index should be within the list";
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based task position
     * @return removed task
     */
    public Task delete(int index) {
        assert isValidIndex(index) : "task index should be within the list";
        return tasks.remove(index);
    }

    /** Returns whether the index identifies an existing task. */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /** Returns whether an equivalent task is already in the list. */
    public boolean containsDuplicate(Task candidate) {
        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword text to find in task descriptions
     * @return a new list containing the matching tasks in their original order
     */
    public TaskList find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matches = tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
        return new TaskList(matches);
    }

    /**
     * Returns a read-only view for display and persistence.
     *
     * @return unmodifiable task view
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
