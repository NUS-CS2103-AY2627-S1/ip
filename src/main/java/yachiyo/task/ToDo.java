package yachiyo.task;

/**
 * Represents a task without a date or time.
 */
public class ToDo extends Task {
    /**
     * Creates an incomplete to-do task with the specified description.
     *
     * @param description Description of the task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns a copy of this to-do with the specified description.
     *
     * @param description replacement description.
     * @return to-do containing the replacement description.
     */
    @Override
    public ToDo withDescription(String description) {
        return copyCompletionStatusTo(new ToDo(description));
    }

    /**
     * Returns this to-do task in the format used by the storage file.
     *
     * @return Stored to-do task representation.
     */
    @Override
    public String toFileFormat() {
        return "TODO | " + super.toFileFormat();
    }

    /**
     * Returns this to-do task in its user-facing display format.
     *
     * @return Formatted to-do task description and status.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
