package tasktracker.task;

/**
 * Represents a {@code ToDo} task without any date or time constraints.
 */
public class ToDo extends Task {
    /**
     * Constructs a {@code ToDo} instance with the specified task description.
     *
     * @param description Text describing the todo task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Formats the todo task for file storage.
     *
     * @return Pipe-delimited string representing the todo task prefixed with {@code "T"}.
     */
    @Override
    public String toFileFormat() {
        return "T | " + toFileFormatPrefix();
    }

    /**
     * Returns the formatted string representation of the todo task.
     *
     * @return String representation containing status and description prefixed with {@code "[T]"}.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
