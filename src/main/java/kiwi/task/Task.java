package kiwi.task;

import java.time.LocalDate;

import kiwi.KiwiException;

/**
 * Represents a task with a description, a {@link TaskType}, and a done/not-done status.
 * Subclasses may add extra details such as deadline or event times.
 */
public class Task {
    /** Separator between fields in a save-file line. */
    public static final String SAVE_FIELD_SEPARATOR = " | ";
    /** Flag written when the task is done. */
    public static final String SAVE_DONE_FLAG = "1";
    /** Flag written when the task is not done. */
    public static final String SAVE_NOT_DONE_FLAG = "0";

    protected String description;
    protected boolean isDone;
    protected TaskType type;

    /**
     * Creates a new task that is not done yet.
     *
     * @param description what the task is about.
     * @param type whether this is a to-do, deadline, or event.
     */
    public Task(String description, TaskType type) {
        assert description != null : "Parser and Storage always supply a description";
        assert type != null : "Subclasses always pass a TaskType";
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /**
     * Rejects descriptions that would break the save-file format.
     *
     * @param description candidate task description.
     * @throws KiwiException If {@code description} contains the save-field separator.
     */
    public static void validateDescription(String description) throws KiwiException {
        assert description != null : "Parser and Storage always supply a description";
        if (description.contains(SAVE_FIELD_SEPARATOR)) {
            throw new KiwiException(
                    "Task descriptions cannot contain \"" + SAVE_FIELD_SEPARATOR + "\".");
        }
    }

    /**
     * Returns whether {@code other} is the same kind of task with the same details.
     * Done status is ignored so marking a task done does not make a second copy unique.
     *
     * @param other task to compare with.
     * @return {@code true} if both tasks would look the same aside from done status.
     */
    public boolean hasSameDetails(Task other) {
        if (other == null || type != other.type) {
            return false;
        }
        return description.equals(other.description);
    }

    /**
     * Returns an icon showing whether the task is done.
     *
     * @return "X" if done, otherwise a space.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return description text.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the task's type (to-do, deadline, or event).
     *
     * @return task type enum value.
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns whether this task is associated with the given calendar date.
     * Default: to-dos have no date, so they never match.
     *
     * @param date date to check.
     * @return true if the task occurs on {@code date}.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task is marked done.
     *
     * @return true if done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns one line for hard-disk storage, e.g. {@code T | 1 | read book}.
     * Subclasses append extra fields after the description.
     *
     * @return save-file line.
     */
    public String toSaveFormat() {
        return type.getIcon() + SAVE_FIELD_SEPARATOR
                + (isDone ? SAVE_DONE_FLAG : SAVE_NOT_DONE_FLAG)
                + SAVE_FIELD_SEPARATOR + description;
    }

    /**
     * Returns a display string such as {@code [T][ ] read book}.
     *
     * @return formatted task text.
     */
    @Override
    public String toString() {
        return type + "[" + getStatusIcon() + "] " + description;
    }
}
