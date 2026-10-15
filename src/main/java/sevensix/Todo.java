package sevensix;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task that is initially not done.
     *
     * @param description the text describing the task.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
