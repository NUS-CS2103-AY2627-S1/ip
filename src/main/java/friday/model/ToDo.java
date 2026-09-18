package friday.model;

/**
 * A simple task without a date or time constraint.
 */
public class ToDo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description Text describing the todo.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Serializes this todo for storage.
     *
     * @return A type marker followed by the common task fields.
     */
    @Override
    public String toFileString() {
        return "T | " + super.toFileString();
    }

    /**
     * Returns the user-facing todo representation.
     *
     * @return The todo type marker, status, and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
