package friday.model;

/**
 * Base type for tasks stored and displayed by Friday.
 */
public class Task {
    /**
     * Human-readable task description.
     */
    protected String description;

    /**
     * Whether this task has been completed.
     */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the supplied description.
     *
     * @param description Text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return The human-readable task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks whether two tasks have the same type and user-visible details.
     *
     * @param other Task to compare with this task.
     * @return Whether both tasks represent the same task details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null && getClass().equals(other.getClass())
                && description.equals(other.description);
    }

    /**
     * Returns the marker used in the task's display representation.
     *
     * @return {@code X} for a completed task, or a space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns whether this task is complete.
     *
     * @return Whether the task has been marked as done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmarkAsDone() {
        this.isDone = false;
    }

    /**
     * Converts the common task fields into the format used in the data file.
     * Subclasses add their task type and any additional fields.
     *
     * @return The completion status and description separated by pipes.
     */
    public String toFileString() {
        return String.format("%d | %s", isDone ? 1 : 0, escapeFileField(description));
    }

    /**
     * Escapes characters that have a special meaning in the task file format.
     * Backslashes are escaped first so newly added pipe escapes remain distinct.
     *
     * @param value A task field to store.
     * @return The field with backslashes and pipe characters escaped.
     */
    protected static String escapeFileField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns the user-facing representation of this task.
     *
     * @return The status icon and description.
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", getStatusIcon(), description);
    }
}
