package greenchonk.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents a task and whether it has been completed.
 */
public abstract class Task {
    private final String description;
    private TaskStatus status;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description.
     */
    protected Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} when completed, or a space otherwise.
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if the task is completed.
     */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether another task has the same type and user-provided details.
     * Completion status is deliberately ignored when detecting duplicates.
     *
     * @param other the task to compare.
     * @return true if both tasks represent the same task details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equals(other.description);
    }

    /**
     * Returns a copy of this task with a replacement description.
     * The copy retains this task's completion status and subtype-specific details.
     *
     * @param description the replacement description.
     * @return the updated task copy.
     */
    public abstract Task withDescription(String description);

    /**
     * Copies this task's completion status to another task.
     *
     * @param task the task that should receive this task's status.
     */
    protected void copyStatusTo(Task task) {
        if (isDone()) {
            task.markAsDone();
        }
    }

    /**
     * Returns whether this task's description contains a keyword, ignoring case.
     *
     * @param keyword the literal keyword to find.
     * @return true if the description contains the keyword.
     */
    public boolean matches(String keyword) {
        String normalizedDescription = description.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return normalizedDescription.contains(normalizedKeyword);
    }

    /**
     * Returns whether this task is scheduled for the given date.
     * Tasks without dates are never scheduled for a particular date.
     *
     * @param date the date to check.
     * @return true if this task occurs on the date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the symbol used to identify this task's type.
     *
     * @return the task type symbol.
     */
    public abstract String getTypeIcon();

    /**
     * Returns the task's type, completion status, and description for display.
     *
     * @return the task's display text
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
