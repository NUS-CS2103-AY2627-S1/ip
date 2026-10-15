package bob.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private final String description;
    private boolean isDone;
    private final Priority priority;

    /**
     * Creates a task that is initially not done.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this(description, Priority.NONE);
    }

    /**
     * Creates an incomplete task with an explicit priority.
     *
     * @param description description of the task
     * @param priority priority tag, or NONE for no tag
     */
    public Task(String description, Priority priority) {
        this.priority = java.util.Objects.requireNonNull(priority);
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task's priority, including NONE for untagged tasks.
     *
     * @return task priority
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Returns an optional trailing storage field, preserving old untagged records.
     *
     * @return priority field or an empty string
     */
    protected String getStoragePriority() {
        return priority == Priority.NONE ? "" : " | " + priority;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the character used to display this task's completion state.
     *
     * @return {@code "X"} if done, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether this task's description contains the given keyword.
     *
     * @param keyword keyword to find
     * @return {@code true} if the description contains the keyword
     */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Returns the completion state in the compact form used for storage.
     *
     * @return {@code "1"} if done, or {@code "0"} otherwise
     */
    protected String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Escapes storage separators so task text can contain pipes or backslashes.
     *
     * @param value field value to escape
     * @return escaped field value
     */
    protected String escapeStorageField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns this task in the application's storage format.
     * Subclasses include their task type and any additional fields.
     *
     * @return one line suitable for writing to the task data file
     */
    public String toStorageString() {
        return getStorageStatus() + " | " + escapeStorageField(description);
    }

    /**
     * Returns this task in the format used by Bob's task list.
     *
     * @return the status icon followed by the task description
     */
    @Override
    public String toString() {
        String tag = priority == Priority.NONE ? "" : "[priority: " + priority + "]";
        return "[" + getStatusIcon() + "]" + tag + " " + description;
    }
}
