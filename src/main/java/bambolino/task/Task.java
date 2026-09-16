package bambolino.task;

import java.util.Locale;

/**
 * Represents one task in the task list.
 * A task has a description and can be marked as completed or incomplete.
 */
public class Task {
    /** The user-provided text describing this task. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with a description.
     *
     * @param description The text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol representing this task's completion status.
     *
     * @return {@code X} when complete, or a space when incomplete.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the text describing this task.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns whether this task description contains a keyword, ignoring case.
     *
     * @param keyword The text to search for.
     * @return True if the description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Converts this task into one line of storage data.
     *
     * @return A storage line containing the task type, status, and fields.
     */
    public String toStorageString() {
        return "T|" + (isDone ? "1" : "0") + "|" + encode(description);
    }

    /** Encodes text safely for use as a pipe-separated storage field. */
    protected String encode(String text) {
        return java.util.Base64.getEncoder().encodeToString(
                text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * Returns this task in the list-display format.
     *
     * @return The completion status icon and task description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
