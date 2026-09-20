package axiom.task;

/**
 * Represents a todo task without any date or time attached.
 */
public class Todo extends Task {
    private static final String TYPE_PREFIX = "[T]";

    /**
     * Creates a new todo with the given description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a string representation with the {@code [T]} type prefix.
     *
     * @return Formatted todo string (e.g. {@code "[T][ ] read book"}).
     */
    @Override
    public String toString() {
        return TYPE_PREFIX + super.toString();
    }
}
