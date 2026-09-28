package baron.core.task;

import java.util.UUID;

/**
 * Represents a task without a deadline or event time.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task with the specified description.
     *
     * @param description The task description.
     */
    public Todo(String description) {
        this(UUID.randomUUID(), description);
    }

    /**
     * Creates a to-do task with the specified identifier and description.
     *
     * @param uuid The task identifier.
     * @param description The task description.
     */
    public Todo(UUID uuid, String description) {
        super(uuid, TaskType.TODO, description);
    }
}
