package wwaffle.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo in its display format.
     *
     * @return Formatted todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
