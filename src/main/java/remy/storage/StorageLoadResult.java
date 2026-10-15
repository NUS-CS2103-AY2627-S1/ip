package remy.storage;

import java.util.List;

import remy.task.TaskList;

/**
 * Contains tasks recovered from a task file and the line numbers rejected during loading.
 */
public class StorageLoadResult {
    /** Tasks successfully recovered from the task file. */
    private final TaskList tasks;

    /** One-based line numbers containing invalid or duplicate task data. */
    private final List<Integer> invalidLineNumbers;

    /**
     * Creates a result for one task-file load attempt.
     *
     * @param tasks tasks successfully recovered from the file
     * @param invalidLineNumbers one-based line numbers containing invalid or duplicate task data
     */
    public StorageLoadResult(TaskList tasks, List<Integer> invalidLineNumbers) {
        assert tasks != null : "Loaded task list must not be null";
        assert invalidLineNumbers != null : "Invalid line-number list must not be null";

        this.tasks = tasks;
        this.invalidLineNumbers = List.copyOf(invalidLineNumbers);
    }

    /**
     * Returns the tasks successfully recovered from the file.
     *
     * @return recovered tasks
     */
    public TaskList getTasks() {
        return tasks;
    }

    /**
     * Returns the one-based line numbers containing invalid or duplicate task data.
     *
     * @return unmodifiable invalid line-number list
     */
    public List<Integer> getInvalidLineNumbers() {
        return invalidLineNumbers;
    }

    /**
     * Returns whether any lines in the task file were invalid or duplicated another task.
     *
     * @return true when at least one line contains invalid or duplicate task data
     */
    public boolean hasInvalidLines() {
        return !invalidLineNumbers.isEmpty();
    }
}
