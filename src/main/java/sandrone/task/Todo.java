package sandrone.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the supplied description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in the format used for display.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns this todo in the format used in the save file.
     */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }
}
