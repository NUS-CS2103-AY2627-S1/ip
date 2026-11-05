package larp;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the specified description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates a todo with its saved completion state.
     *
     * @param description Description of the todo.
     * @param isDone Whether the todo has been completed.
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    /**
     * Returns a display-friendly representation of this todo.
     *
     * @return Formatted todo description and status.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
