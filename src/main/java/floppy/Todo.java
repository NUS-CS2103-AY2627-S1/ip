package floppy;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the specified description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
