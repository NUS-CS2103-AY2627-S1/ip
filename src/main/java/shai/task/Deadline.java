package shai.task;

import java.time.LocalDateTime;

import shai.parser.DateTimeParser;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The date or time by which the task should be completed. */
    protected LocalDateTime by;

    /** The number of minutes before the deadline when its reminder should appear. */
    protected long reminderMinutesBefore;

    /**
     * Creates a Deadline that is initially not done.
     *
     * @param description the text describing the Deadline
     * @param by the date or time by which the task should be completed
     */
    public Deadline(String description, LocalDateTime by) {
        this(description, by, Reminder.DEFAULT_MINUTES_BEFORE);
    }

    /**
     * Creates a Deadline with a specified reminder lead time.
     *
     * @param description the text describing the Deadline
     * @param by the date or time by which the task should be completed
     * @param reminderMinutesBefore minutes before the deadline, or {@link Reminder#DISABLED}
     * @throws IllegalArgumentException if the deadline or reminder value is invalid
     */
    public Deadline(String description, LocalDateTime by, long reminderMinutesBefore) {
        super(description);
        if (by == null) {
            throw new IllegalArgumentException("A deadline must have a date or time.");
        }
        if (!Reminder.isValidMinutesBefore(reminderMinutesBefore)) {
            throw new IllegalArgumentException("A deadline must have a valid reminder value.");
        }
        this.by = by;
        this.reminderMinutesBefore = reminderMinutesBefore;
    }

    /**
     * Gets the date or time by which this task should be completed.
     *
     * @return the deadline value
     */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns the number of minutes before this deadline for its reminder. */
    public long getReminderMinutesBefore() {
        return reminderMinutesBefore;
    }

    /** Sets the number of minutes before this deadline for its reminder. */
    public void setReminderMinutesBefore(long reminderMinutesBefore) {
        if (!Reminder.isValidMinutesBefore(reminderMinutesBefore)) {
            throw new IllegalArgumentException("A deadline must have a valid reminder value.");
        }
        this.reminderMinutesBefore = reminderMinutesBefore;
    }

    /**
     * Returns the Deadline with its type, completion status, and due time.
     *
     * @return the formatted Deadline description
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.formatForDisplay(by) + ")";
    }
}
