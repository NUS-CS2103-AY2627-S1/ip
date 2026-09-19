package ollie;

/**
 * Represents a task without an associated date.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a display-friendly representation of this todo.
     *
     * @return Formatted todo.
     */
    @Override
    public String toString() {
        return "[todo]" + super.toString();
    }
}
