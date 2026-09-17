package sace;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo with its type and completion status.
     *
     * @return displayable representation of this todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
