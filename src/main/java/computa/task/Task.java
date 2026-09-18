package computa.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents one task in Computa's in-memory task list.
 */
public class Task {
    /** The text entered by the user for this task. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task.
     */
    public Task(String description) {
        assert description != null && !description.isBlank() : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns this task in the format used by the task data file.
     *
     * @return status and description fields separated by vertical bars.
     */
    public String toFileFormat() {
        return " | " + (isDone ? "1" : "0") + " | " + description;
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
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a completed task, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task description.
     *
     * @return the description entered by the user.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the type icon for this task.
     *
     * @return the one-letter icon used in task lists.
     */
    public String getTypeIcon() {
        return "T";
    }

    /**
     * Returns the description and any date/time details for display outside a list.
     *
     * @return the task's display description.
     */
    public String getDisplayDescription() {
        return description;
    }

    /**
     * Returns whether another task has the same type, description, and schedule.
     * Completion status is intentionally ignored so a completed task cannot be
     * added again as an incomplete duplicate.
     *
     * @param other task to compare with this task.
     * @return true when both tasks represent the same task details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equals(other.description)
                && hasSameSchedule(other);
    }

    /**
     * Returns whether another task of the same type has the same schedule.
     * Tasks without dates have no extra schedule details to compare.
     *
     * @param other task of the same type.
     * @return true when the schedule details match.
     */
    protected boolean hasSameSchedule(Task other) {
        return true;
    }

    /**
     * Checks whether this task occurs on a date. ToDos have no date, so they never match.
     *
     * @param date date to check.
     * @return false for a task without date information.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the date used to order this task, if it has a parsed date.
     *
     * @return the task's sort date, or {@code null} when it has no parsed date.
     */
    public LocalDateTime getSortDate() {
        return null;
    }

    /**
     * Formats this task for display in a task list.
     *
     * @return type icon, completion icon, and description.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + getDisplayDescription();
    }
}
