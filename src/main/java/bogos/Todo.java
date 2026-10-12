package bogos;

import java.util.List;

/**
 * Represents a to-do task.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task with the given description.
     *
     * @param description Description of the to-do.
     * @throws IllegalArgumentException If the description is blank.
     */
    public Todo(String description) {
        this(description, List.of());
    }

    /**
     * Creates an incomplete to-do with the given description and tags.
     *
     * @param description Description of the to-do.
     * @param tags Tags assigned to the to-do.
     * @throws IllegalArgumentException If the description or a tag is invalid.
     */
    public Todo(String description, List<String> tags) {
        super(TaskType.TODO, description, tags);
    }
}
