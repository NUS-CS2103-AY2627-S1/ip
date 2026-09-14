package groot.task;

import java.util.Locale;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** Human-readable details of the task. */
    protected String description;

    /** Whether the task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description without its type or status decorations.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the character used to display the task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether this task's description contains the given keyword.
     * Matching is case-insensitive so users need not remember the original casing.
     *
     * @param keyword Keyword or phrase to search for.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean matches(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Escapes characters that have structural meaning in the data file.
     *
     * @param value Task text to store.
     * @return Text safe for the pipe-separated storage format.
     */
    protected static String escapeDataField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns the task in the format used by the local data file.
     *
     * @return Pipe-separated task data.
     */
    public String toDataString() {
        return (isDone ? "1" : "0") + " | " + escapeDataField(description);
    }

    /**
     * Returns the task in its display format, including its status icon.
     *
     * @return Formatted task text.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
