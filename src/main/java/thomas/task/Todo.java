package thomas.task;

/**
 * Represents a todo task with no date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description Textual description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a formatted string for file storage.
     *
     * @return Formatted string representing this todo task.
     */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }

    /**
     * Returns the status and description of this todo task.
     *
     * @return Status icon and description as a string.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
