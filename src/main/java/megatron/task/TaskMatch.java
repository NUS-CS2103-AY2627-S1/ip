package megatron.task;

import java.util.Objects;

/**
 * Represents a task that matched a search and its original list number.
 *
 * @param taskNumber The original one-based task number.
 * @param task The matching task.
 */
public record TaskMatch(int taskNumber, Task task) {
    /**
     * Creates a numbered search match.
     */
    public TaskMatch {
        if (taskNumber < 1) {
            throw new IllegalArgumentException("A task number must be positive.");
        }
        Objects.requireNonNull(task);
    }
}
