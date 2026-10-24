package orbit.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo.
     *
     * @param description description shown to the user
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates a todo with a stored completion status.
     *
     * @param description description shown to the user
     * @param isDone whether the todo is completed
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    /**
     * Formats this todo for display to the user.
     *
     * @return todo marker, completion status, and description
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
