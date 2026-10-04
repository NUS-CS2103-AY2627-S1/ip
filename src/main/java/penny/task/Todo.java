package penny.task;

/**
 * Represents a basic todo task without any date or time constraints.
 */
public class Todo extends Task {

    /** Single-character type code identifying a todo task in storage. */
    public static final String TYPE_CODE = "T";

    /**
     * Constructs a Todo task with the given description.
     *
     * @param description The task description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Formats the todo task into a pipe-delimited string for disk persistence.
     *
     * @return Formatted file storage string (e.g., "T | 0 | read book").
     */
    @Override
    public String toFileFormat() {
        return toFileFormatPrefix(TYPE_CODE);
    }

    /**
     * Returns the string representation of the todo task for display.
     *
     * @return Formatted todo string (e.g., "[T][ ] read book").
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
