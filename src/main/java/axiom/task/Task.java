package axiom.task;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a task with a description and done status.
 */
public class Task {
    private static final String DONE_ICON = "X";
    private static final String NOT_DONE_ICON = " ";

    private final String description;
    private boolean isDone;

    /**
     * Creates a new task that is initially not done.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        // Parser and Storage reject empty descriptions; reaching here with a blank value is a bug.
        assert description != null && !description.isBlank()
                : "Task description should not be null or blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the date used when ordering this task chronologically.
     * Tasks without a date return an empty result.
     *
     * @return The chronological date, or empty if this task has none.
     */
    public Optional<LocalDateTime> getChronologicalDate() {
        return Optional.empty();
    }

    /**
     * Returns the task description.
     *
     * @return The description text.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is marked as done.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the status icon shown in task output.
     *
     * @return {@code "X"} if done, or a single space if not done.
     */
    public String getStatusIcon() {
        return (isDone ? DONE_ICON : NOT_DONE_ICON);
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
     * Returns whether this task has the same type and identifying details as {@code other}.
     * Done status is ignored so a marked copy still counts as the same task.
     *
     * @param other Task to compare.
     * @return {@code true} if both tasks represent the same work item.
     */
    public boolean hasSameDetails(Task other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return description.equals(other.description);
    }

    /**
     * Returns a string representation of this task's status and description.
     *
     * @return Formatted status and description (e.g. {@code "[ ] read book"}).
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
