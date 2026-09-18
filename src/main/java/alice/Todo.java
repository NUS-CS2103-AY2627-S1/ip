package alice;

/**
 * Represents a simple task with only a description and no associated
 * date or time.
 */
public class Todo extends Task {
    /**
     * Constructs a Todo task.
     *
     * @param description description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the display name of this task type.
     *
     * @return "Todo".
     */
    @Override
    public String getTypeName() {
        return "Todo";
    }

    /**
     * Returns a string representation of this task suitable for saving to
     * the data file.
     *
     * @return formatted string for file storage.
     */
    @Override
    public String toFileFormat() {
        return "T | " + (isDone() ? "1" : "0") + " | " + getDescription();
    }

    /**
     * Returns a string representation of this todo task, including its
     * type marker, completion status, and description.
     *
     * @return formatted string for display to the user.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
