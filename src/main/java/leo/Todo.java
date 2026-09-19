package leo;

/**
 * Represents a simple to-do task: a description with no associated
 * date or time.
 */
public class Todo extends Task {
    /**
     * Creates a new, not-done todo with the given description.
     *
     * @param description description of the todo
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the type code for a todo.
     *
     * @return "T"
     */
    @Override
    public String getType() {
        return "T";
    }
}
