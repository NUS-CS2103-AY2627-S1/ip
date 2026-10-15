package larry.task;

import java.time.LocalDate;

/**
 * Represents a task and its completion state.
 */
public class Task {
    private String description;
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

    /**
     * Returns the character used to display the task's completion state.
     *
     * @return {@code X} when done, or a space when not done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task description.
     *
     * @return Description of the task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Replaces this task's description.
     *
     * @param description New nonblank description.
     * @throws IllegalArgumentException If the description is blank.
     */
    public void updateDescription(String description) {
        if (description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be blank");
        }
        this.description = description;
    }

    /**
     * Checks whether the task is completed.
     *
     * @return True when the task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Checks whether this task occurs on a date.
     * Tasks without date information never occur on a specific date.
     *
     * @param date Date to check.
     * @return True when the task occurs on the date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the task in the format used by Larry's responses.
     *
     * @return Status icon followed by the task description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
