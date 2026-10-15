package zeus.task;

/** Represents a task and whether it has been completed. */
public class Task {
    /** Description shown for this task. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     * @return {@code X} when completed, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Escapes characters that have special meaning in the data file.
     * @param value task field to escape
     * @return escaped field safe for pipe-separated storage
     */
    protected static String escapeDataField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns the common fields used when saving this task.
     * @return completion status and description separated by {@code |}
     */
    public String toDataString() {
        return (isDone ? "1" : "0") + " | " + escapeDataField(description);
    }

    /**
     * Returns this task in the format displayed by Zeus.
     * @return the status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
