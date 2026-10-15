package jonk;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the task's display representation.
     *
     * @return Task status and description.
     */
    @Override
    public String toString() {
        return "[" + (isDone ? "X" : " ") + "] " + description;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsUndone() {
        isDone = false;
    }

    public boolean isDone() {
        return isDone;
    }

    /**
     * Converts the common task fields to their data-file representation.
     * Subclasses prepend their type and append any extra fields.
     *
     * @return Completion status and description separated by {@code |}.
     */
    public String toFileString() {
        return (isDone ? "1" : "0") + " | " + encodeFileField(description);
    }

    /**
     * Escapes characters that otherwise have special meaning in the data file.
     *
     * @param field Task text to encode.
     * @return Encoded text safe for the pipe-separated file format.
     */
    protected static String encodeFileField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }
}
