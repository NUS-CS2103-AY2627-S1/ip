package vega;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Owns Vega's task collection and validates numbered task access.
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
     * Creates a task list containing the loaded tasks.
     *
     * @param tasks Tasks loaded from persistent storage.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        assert tasks.stream().noneMatch(task -> task == null) : "Task list must not contain null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /**
     * Returns the task at the given one-based task number.
     *
     * @param taskNumber One-based position of the task.
     * @return Task at the requested position.
     * @throws VegaException If the task number is outside the list.
     */
    public Task get(int taskNumber) throws VegaException {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Removes and returns the task at the given one-based task number.
     *
     * @param taskNumber One-based position of the task.
     * @return Removed task.
     * @throws VegaException If the task number is outside the list.
     */
    public Task delete(int taskNumber) throws VegaException {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of stored tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot for display and storage.
     *
     * @return Snapshot of the tasks in their current order.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     * Matching is case-insensitive.
     *
     * @param keyword Text to find in task descriptions.
     * @return Tasks with descriptions containing the keyword.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        return tasks.stream().
                filter(task -> task.getDescription().
                        toLowerCase(Locale.ENGLISH).
                        contains(normalizedKeyword)).
                toList();
    }

    private int toIndex(int taskNumber) throws VegaException {
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new VegaException("There is no task numbered " + taskNumber + ".");
        }
        return taskIndex;
    }
}
