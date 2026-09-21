package friday.task;

import java.time.LocalDate;

/**
 * A task's description and completion status, shared by all task types.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task that is initially not done.
     *
     * @param description text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description without status or type markers.
     *
     * @return the description supplied at construction.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the completion marker used in the console task display.
     *
     * @return {@code "X"} when done, or a single space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Reports whether the task has been completed.
     *
     * @return {@code true} if the task is marked done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as done; an already completed task remains done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done; an incomplete task remains not done.
     */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Checks whether this task occurs on a calendar date.
     * Dated task types override this method to provide their matching rules.
     *
     * @param date calendar date to check.
     * @return {@code false}, because this base task has no date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the completion marker and description for display.
     *
     * @return the task text, such as {@code [X] read book}.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
