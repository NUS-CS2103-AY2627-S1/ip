package monday.task;

/**
 * Represents a todo task.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo task with the given description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates an incomplete todo task with the given description and notes.
     */
    public Todo(String description, String notes) {
        super(description, notes);
    }

    /**
     * Creates a todo task with the given description and completion status.
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    /**
     * Creates a todo task with the given description, completion status, and notes.
     */
    public Todo(String description, boolean isDone, String notes) {
        super(description, isDone, notes);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
