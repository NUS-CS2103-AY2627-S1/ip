package alice.task;

import java.time.LocalDateTime;

/**
 * Represents the shared state of every concrete task type.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;
    private Recurrence recurrence;

    /**
     * Creates a task with the given description.
     *
     * @param description The task description.
     */
    public Task(String description) {
        assert description != null && !description.isBlank()
                : "Tasks must have a non-blank description.";
        this.description = description;
        this.isDone = false;
        this.recurrence = null;
    }

    /**
     * Returns the icon representing the task's completion status.
     *
     * @return {@code [X]} when complete, otherwise {@code [ ]}.
     */
    public String getStatusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task repeats after it is completed. */
    public boolean isRecurring() {
        return recurrence != null;
    }

    /** Returns this task's recurrence interval. */
    public Recurrence getRecurrence() {
        return recurrence;
    }

    /**
     * Makes this task recur at the supplied interval.
     *
     * @param recurrence The interval between occurrences.
     */
    public void setRecurrence(Recurrence recurrence) {
        assert recurrence != null : "A recurring task requires an interval.";
        this.recurrence = recurrence;
    }

    /** Removes this task's recurrence interval. */
    public void clearRecurrence() {
        recurrence = null;
    }

    /**
     * Creates the first future occurrence of this recurring task.
     *
     * @param currentDateTime The date and time at which the task is completed.
     * @return The next occurrence, with the same description and recurrence.
     */
    public abstract Task createNextOccurrence(LocalDateTime currentDateTime);

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns the task status and description for display.
     *
     * @return A display representation of this task.
     */
    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }
}
