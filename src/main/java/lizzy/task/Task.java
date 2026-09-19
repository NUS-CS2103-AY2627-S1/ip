package lizzy.task;

import java.time.LocalDate;

/**
 * Represents common data and behavior shared by all task types.
 */
public class Task {
    /**
     * The text describing the task.
     */
    private final String description;

    /**
     * Whether the task has been completed.
     */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this(description, false);
    }

    /**
     * Restores a task with its saved completion state.
     *
     * @param description the text describing the task
     * @param isDone whether the task has been completed
     */
    protected Task(String description, boolean isDone) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A task description cannot be blank.");
        }
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Returns whether another task has the same concrete type and user-entered details.
     * Completion state is deliberately ignored because marking a task does not make it a different task.
     *
     * @param other the task to compare
     * @return {@code true} if both tasks describe the same matter
     */
    boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equals(other.description);
    }

    /**
     * Returns the symbol used to show this task's completion status.
     *
     * @return {@code "X"} if the task is complete, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
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
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is complete
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether this task is scheduled on the given date.
     * Tasks without dates are never included in date-based searches.
     *
     * @param date the date to check
     * @return {@code true} if the task occurs on that date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns a display-ready representation of this task.
     *
     * @return the task status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }
}
