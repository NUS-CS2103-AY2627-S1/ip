package kibo.task;

import java.time.LocalDate;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** Description entered by the user. */
    private final String description;
    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        assert description != null : "A task description must not be null";
        assert !description.isBlank() : "A task description must not be blank";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} if completed, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's description for storage.
     *
     * @return task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether this task belongs to the schedule for a date.
     *
     * @param date date whose schedule is being viewed.
     * @return {@code false}, as a general task has no scheduled date.
     */
    public boolean isScheduledOn(LocalDate date) {
        assert date != null : "A schedule date must not be null";
        return false;
    }

    /**
     * Returns the task description with its completion status.
     *
     * @return task text in the form {@code [status] description}.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
