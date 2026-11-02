package megia.model;

import java.util.Objects;

/**
 * Associates a task with its one-based user-facing ID.
 *
 * @param id One-based task ID.
 * @param task Task associated with the ID.
 */
public record TaskEntry(int id, Task task) {
    /**
     * Creates a task entry after validating its identity and task.
     */
    public TaskEntry {
        if (id < 1) {
            throw new IllegalArgumentException("Task IDs must be positive");
        }
        Objects.requireNonNull(task);
    }
}
