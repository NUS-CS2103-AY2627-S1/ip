package shai.task;

import java.time.LocalDateTime;

import shai.parser.DateTimeParser;

/**
 * Represents a task that takes place between a start and end date or time.
 */
public class Event extends Task {
    /** The date or time when the event starts. */
    protected LocalDateTime from;

    /** The date or time when the event ends. */
    protected LocalDateTime to;

    /** The number of minutes before the event when its reminder should appear. */
    protected long reminderMinutesBefore;

    /**
     * Creates an Event that is initially not done.
     *
     * @param description the text describing the Event
     * @param from the date or time when the event starts
     * @param to the date or time when the event ends
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        this(description, from, to, Reminder.DEFAULT_MINUTES_BEFORE);
    }

    /**
     * Creates an Event with a specified reminder lead time.
     *
     * @param description the text describing the Event
     * @param from the date or time when the event starts
     * @param to the date or time when the event ends
     * @param reminderMinutesBefore minutes before the event, or {@link Reminder#DISABLED}
     * @throws IllegalArgumentException if an event time or reminder value is invalid
     */
    public Event(String description, LocalDateTime from, LocalDateTime to, long reminderMinutesBefore) {
        super(description);
        if (from == null) {
            throw new IllegalArgumentException("An event must have a starting date or time.");
        }
        if (to == null) {
            throw new IllegalArgumentException("An event must have an ending date or time.");
        }
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("An event must end after it starts.");
        }
        if (!Reminder.isValidMinutesBefore(reminderMinutesBefore)) {
            throw new IllegalArgumentException("An event must have a valid reminder value.");
        }
        this.from = from;
        this.to = to;
        this.reminderMinutesBefore = reminderMinutesBefore;
    }

    /**
     * Gets the date or time when this event starts.
     *
     * @return the event start value
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Gets the date or time when this event ends.
     *
     * @return the event end value
     */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns the number of minutes before this event for its reminder. */
    public long getReminderMinutesBefore() {
        return reminderMinutesBefore;
    }

    /** Sets the number of minutes before this event for its reminder. */
    public void setReminderMinutesBefore(long reminderMinutesBefore) {
        if (!Reminder.isValidMinutesBefore(reminderMinutesBefore)) {
            throw new IllegalArgumentException("An event must have a valid reminder value.");
        }
        this.reminderMinutesBefore = reminderMinutesBefore;
    }

    /**
     * Returns the Event with its type, completion status, and time range.
     *
     * @return the formatted Event description
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + DateTimeParser.formatForDisplay(from)
                + " to: " + DateTimeParser.formatForDisplay(to) + ")";
    }
}
