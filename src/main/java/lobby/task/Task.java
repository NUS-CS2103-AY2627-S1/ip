package lobby.task;

/**
 * Represents one task in the task list and whether it has been completed.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon used when listing this task.
     *
     * @return {@code "X"} if this task is done, otherwise a blank space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Reports whether this task has been completed.
     *
     * @return {@code true} when the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Escapes separator and backslash characters so a field can be stored safely.
     *
     * @param field the field to escape.
     * @return the escaped field
     */
    protected static String escapeDataField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Formats the fields shared by every task for storage.
     *
     * @return the completion flag and description separated by {@code |}
     */
    protected String toDataFields() {
        return (isDone ? "1" : "0") + " | " + escapeDataField(description);
    }

    /**
     * Formats this task as one line in the save file.
     * Subclasses override this method to include their task type and details.
     *
     * @return the serialized task
     */
    public String toDataString() {
        return toDataFields();
    }

    /**
     * Formats the shared completion status and description of a task.
     * Subclasses add their task-type marker and any date/time details.
     *
     * @return the completion status and description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
