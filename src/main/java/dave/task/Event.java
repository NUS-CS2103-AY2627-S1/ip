package dave.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import dave.exception.DaveCommandException;

/**
 * Represents a task that occurs within a specific time interval.
 */
public class Event extends Task {

    /** Formatter for displaying date-only event boundaries. */
    private static final DateTimeFormatter DATE_ONLY_OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy");
    /** Formatter for displaying date and time event boundaries. */
    private static final DateTimeFormatter DATE_TIME_OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");
    /** Formatter for serializing date-time event boundaries to persistent storage. */
    private static final DateTimeFormatter DATE_TIME_FILE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Start date and optional time of the event. */
    private LocalDateTime from;
    /** Indicates whether the start time of day was explicitly specified. */
    private boolean hasFromTime;
    /** End date and optional time of the event. */
    private LocalDateTime to;
    /** Indicates whether the end time of day was explicitly specified. */
    private boolean hasToTime;

    /**
     * Constructs a new Event task with full date-time and time presence information.
     *
     * @param description Description of the event.
     * @param from Start date and time.
     * @param hasFromTime True if start time of day was specified, false if date only.
     * @param to End date and time.
     * @param hasToTime True if end time of day was specified, false if date only.
     * @throws DaveCommandException If the start date-time is equal to or later than the end date-time.
     */
    public Event(String description, LocalDateTime from, boolean hasFromTime,
                 LocalDateTime to, boolean hasToTime) {
        super(description);
        assert from != null : "Event start date-time cannot be null";
        assert to != null : "Event end date-time cannot be null";
        validateChronology(from, to);
        this.from = from;
        this.hasFromTime = hasFromTime;
        this.to = to;
        this.hasToTime = hasToTime;
    }

    /**
     * Constructs a new Event task with start and end date-times with times of day.
     *
     * @param description Description of the event.
     * @param from Start date and time.
     * @param to End date and time.
     * @throws DaveCommandException If the start date-time is equal to or later than the end date-time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        this(description, from, true, to, true);
    }

    /**
     * Constructs a new Event task with date-only start and end bounds.
     *
     * @param description Description of the event.
     * @param from Start date.
     * @param to End date.
     * @throws DaveCommandException If the start date is equal to or later than the end date.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        this(description, from.atStartOfDay(), false, to.atStartOfDay(), false);
    }

    /**
     * Validates that the start date-time is strictly earlier than the end date-time.
     *
     * @param start Start date-time.
     * @param end End date-time.
     * @throws DaveCommandException If start date-time is after or equal to end date-time.
     */
    private static void validateChronology(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new DaveCommandException("Pardon, Sire! Event start date/time must be strictly earlier "
                    + "than end date/time.");
        }
    }

    @Override
    public boolean canSnooze() {
        return true;
    }

    /**
     * Returns the start date and time of the event.
     *
     * @return Event start LocalDateTime object.
     */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /**
     * Returns whether the start time of day was explicitly specified.
     *
     * @return True if start time was specified, false otherwise.
     */
    public boolean hasFromTime() {
        return this.hasFromTime;
    }

    /**
     * Returns the end date and time of the event.
     *
     * @return Event end LocalDateTime object.
     */
    public LocalDateTime getTo() {
        return this.to;
    }

    /**
     * Returns whether the end time of day was explicitly specified.
     *
     * @return True if end time was specified, false otherwise.
     */
    public boolean hasToTime() {
        return this.hasToTime;
    }

    /**
     * Reschedules the event with new start and end date-times.
     *
     * @param from New start date and time.
     * @param hasFromTime True if start time was specified, false if date only.
     * @param to New end date and time.
     * @param hasToTime True if end time was specified, false if date only.
     * @throws DaveCommandException If the new start date-time is equal to or later than the end date-time.
     */
    public void reschedule(LocalDateTime from, boolean hasFromTime, LocalDateTime to, boolean hasToTime) {
        assert from != null : "Event start date-time cannot be null";
        assert to != null : "Event end date-time cannot be null";
        validateChronology(from, to);
        this.from = from;
        this.hasFromTime = hasFromTime;
        this.to = to;
        this.hasToTime = hasToTime;
    }

    /**
     * Postpones both start and end of the event by the specified duration, preserving event duration.
     *
     * @param amount Amount of time units to add.
     * @param unit Unit of time to add.
     */
    public void snoozeBy(long amount, ChronoUnit unit) {
        assert unit != null : "ChronoUnit cannot be null when snoozing";
        this.from = this.from.plus(amount, unit);
        this.to = this.to.plus(amount, unit);
    }

    @Override
    public boolean isSameTask(Task other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Event)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return this.getDescription().equalsIgnoreCase(otherEvent.getDescription())
                && this.from.equals(otherEvent.from)
                && this.to.equals(otherEvent.to);
    }

    @Override
    public String toFileFormat() {
        String fromString = this.hasFromTime
                ? this.from.format(DATE_TIME_FILE_FORMATTER)
                : this.from.toLocalDate().toString();
        String toString = this.hasToTime
                ? this.to.format(DATE_TIME_FILE_FORMATTER)
                : this.to.toLocalDate().toString();
        return String.format("E | %d | %s | %s | %s",
                this.isDone() ? 1 : 0, this.getDescription(), fromString, toString);
    }

    @Override
    public String toString() {
        String fromString = this.hasFromTime
                ? this.from.format(DATE_TIME_OUTPUT_FORMATTER)
                : this.from.format(DATE_ONLY_OUTPUT_FORMATTER);
        String toString = this.hasToTime
                ? this.to.format(DATE_TIME_OUTPUT_FORMATTER)
                : this.to.format(DATE_ONLY_OUTPUT_FORMATTER);
        return String.format("[E]%s (from: %s to: %s)", super.toString(), fromString, toString);
    }
}
