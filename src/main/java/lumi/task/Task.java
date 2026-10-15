package lumi.task;

import java.util.Locale;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** A short description of the work to be done. */
    private final String description;

    /** Whether this task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
    * @param description Description of the task.
     */
    public Task(String description) {
        assert description != null && !description.isBlank()
                : "Task descriptions must be validated before task creation";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to show the task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task description for storage and other non-UI uses.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Reports whether this task has been completed.
     *
     * @return True if the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Reports whether the task description contains a keyword, ignoring case.
     *
     * @param keyword Non-empty keyword to search for.
    * @return True if the description contains the keyword.
     */
    public boolean hasDescriptionContaining(String keyword) {
        assert keyword != null && !keyword.isBlank()
                : "Search keywords must be validated before matching tasks";
        String normalizedDescription = description.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return normalizedDescription.contains(normalizedKeyword);
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task in the format used when displaying task lists.
     *
     * @return The status icon followed by the task description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
