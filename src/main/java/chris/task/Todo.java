package chris.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description Description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo task in the format used for persistent storage.
     *
     * @return Serialized todo task.
     */
    @Override
    public String toDataString() {
        return "T | " + getStatusNumber() + " | " + this.description;
    }

    /**
     * Returns this todo task in its display format.
     *
     * @return The todo type, status, and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
