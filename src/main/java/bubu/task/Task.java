package bubu.task;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a task that can be marked as complete.
 */
public abstract class Task {
    private static final String STATUS_COMPLETED = "[X] ";
    private static final String STATUS_NOT_COMPLETED = "[ ] ";
    /** Description shown to the user. */
    private String description;
    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the supplied description.
     *
     * @param description task description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns this task's description.
     *
     * @return task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns whether this task is complete.
     *
     * @return true if this task is complete
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Returns the time at which this task should appear in a reminder query.
     *
     * @return scheduled task time, or an empty value for unscheduled tasks.
     */
    public Optional<LocalDateTime> getReminderTime() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return (this.isDone ? STATUS_COMPLETED : STATUS_NOT_COMPLETED)
                + this.description;
    }
}
