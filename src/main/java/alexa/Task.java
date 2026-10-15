package alexa;

import java.util.Locale;

/**
 * Represents a task that can be completed or left incomplete.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Returns whether this task description contains the given keyword, ignoring case.
     *
     * @param keyword The keyword to search for.
     * @return Whether the description contains the keyword.
     */
    public boolean hasDescriptionContaining(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Creates a task with the given description.
     *
     * @param description The task description.
     */
    protected Task(String description) {
        this.description = description;
    }

    /**
     * Returns whether another task has the same identity as this task.
     *
     * @param other The task to compare with this task.
     * @return {@code true} if both tasks have the same type and description.
     */
    public boolean hasSameIdentity(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equals(other.description);
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns the status icon for this task.
     *
     * @return {@code X} when completed, otherwise a space.
     */
    protected String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the common description of this task.
     *
     * @return The task description.
     */
    protected String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is complete.
     */
    protected boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task in the format used by Alexa's data file.
     *
     * @return A single line that can later be used to recreate this task.
     */
    public abstract String toStorageString();

    /**
     * Returns this task in the format shown to the user.
     *
     * @return Formatted task details.
     */
    @Override
    public abstract String toString();
}
