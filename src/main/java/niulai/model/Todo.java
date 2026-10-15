package niulai.model;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates a new incomplete todo task.
     *
     * @param description the text describing the task
     */
    public Todo(String description) {
        super(description);
    }
}
