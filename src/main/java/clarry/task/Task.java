package clarry.task;

import java.time.LocalDate;

/**
 * Represents the shared information and behaviour of a task.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    protected Task(String description) {
        assert description != null && !description.isBlank() : "A task must have a non-blank description";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to show this task's completion status.
     *
     * @return {@code "X"} if the task is done, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns this task's description.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Compares task type, description and schedule, ignoring completion status.
     *
     * @param other task to compare
     * @return whether both tasks have exactly the same details
     */
    public boolean hasSameDetails(Task other) {
        return toFileFormat().replaceFirst(" \\| [01] \\| ", " | ")
                .equals(other.toFileFormat().replaceFirst(" \\| [01] \\| ", " | "));
    }

    /**
     * Returns whether this task occurs on the specified date.
     * Undated tasks do not occur on any date.
     *
     * @param date date to check
     * @return whether this task occurs on the date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Converts this task to the format used in Clarry's save file.
     *
     * @return pipe-separated task data for a todo task
     */
    public String toFileFormat() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }
}
