package anaconda.task;

import java.time.LocalDate;

/**
 * Represents a task that can be marked as done or not done.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
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
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns the description used to identify this task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Reports whether this task has been completed.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the date when this task ends, or {@code null} if it has no end date.
     *
     * @return End date, or {@code null} for an undated task.
     */
    public LocalDate getEndDate() {
        return null;
    }

    /**
     * Formats the task using its completion status and description.
     *
     * @return Display representation of this task.
     */
    @Override
    public String toString() {
        String statusIcon = isDone ? "X" : " ";
        return "[" + statusIcon + "] " + description;
    }
}
