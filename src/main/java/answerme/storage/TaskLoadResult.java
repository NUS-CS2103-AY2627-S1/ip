package answerme.storage;

import java.util.List;

import answerme.task.Task;

/**
 * Represents the outcome of loading tasks from storage.
 *
 * @param taskList The tasks that were loaded successfully.
 * @param warnings The warnings encountered while loading other tasks.
 */
public record TaskLoadResult(List<Task> taskList, List<String> warnings) {
    /**
     * Creates a task-loading result whose task and warning lists cannot be
     * structurally modified.
     */
    public TaskLoadResult {
        taskList = List.copyOf(taskList);
        warnings = List.copyOf(warnings);
    }

    /**
     * Returns whether any saved tasks could not be loaded.
     *
     * @return {@code true} if loading warnings were encountered.
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
}
