package larry;

/**
 * Represents a todo task without a specified date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the specified description.
     *
     * @param description Description of the todo task.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
