package suki;

import java.util.Optional;

/**
 * Represents a task that can be tracked and marked as done.
 *
 * <p>This is the base of the task hierarchy and is complete on its own, but in
 * practice one of the subclasses is used: {@link Todo}, {@link Deadline} or
 * {@link Event}. Subclasses vary the type icon and add their own dates.
 */
public class Task {
    /** What the user wants to do, exactly as they typed it. */
    protected String description;

    /** Whether the user has marked this task as completed. */
    protected boolean isDone;

    /**
     * Creates a task that is not yet done.
     *
     * @param description what the user wants to do
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not yet completed. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the description without any of the display decoration.
     *
     * @return the description as the user typed it
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the marker shown inside the status brackets.
     *
     * @return "X" if the task is done, or a space if it is not
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the single letter identifying the kind of task, which is also
     * the type field written to the save file.
     *
     * <p>Subclasses override this; the base class has no letter of its own.
     *
     * @return the type letter, e.g. "T" for a todo
     */
    public String getTypeIcon() {
        return " ";
    }

    /**
     * Returns the date this task is scheduled against, if it has one.
     *
     * <p>A plain task has no date, so the base class returns an empty
     * Optional and the dated subclasses override this. Asking the task itself
     * lets sorting treat every kind of task the same way, instead of testing
     * for each subclass and reaching for a different getter each time.
     *
     * @return the date used when ordering tasks by time, or empty if the task
     *         has no date
     */
    public Optional<DateTime> getScheduledDateTime() {
        return Optional.empty();
    }

    /**
     * Returns the task as shown to the user, e.g. {@code [T][X] read book}.
     *
     * @return the display text for this task
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
