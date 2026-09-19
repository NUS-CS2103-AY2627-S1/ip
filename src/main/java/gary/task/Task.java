package gary.task;

import java.util.Locale;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /** Description of the task. */
    private final String description;
    /** Whether the task is complete. */
    private boolean isDone;

    /**
     * Creates an incomplete task.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description should not be null";
        assert !description.isBlank() : "Task description should not be blank";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to display the completion status.
     *
     * @return {@code X} when done, or a space when not done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task is complete.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks the task as complete.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks the task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether the task description contains the keyword, ignoring case.
     *
     * @param keyword Keyword to find in the description.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        assert keyword != null : "Search keyword should not be null";
        assert !keyword.isBlank() : "Search keyword should not be blank";

        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the task status and description for display.
     *
     * @return Formatted task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
