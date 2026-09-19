package lizzy.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents a task that starts and ends at specified times.
 */
public class Event extends Task {
    /**
     * The format used to present parsed dates to the user.
     */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    /**
     * The event start date.
     */
    protected final LocalDate from;

    /**
     * The event end date.
     */
    protected final LocalDate to;

    /**
     * The optional start time of a single-day event.
     */
    private final LocalTime startTime;

    /**
     * The optional end time of a single-day event.
     */
    private final LocalTime endTime;

    /**
     * Creates an incomplete event.
     *
     * @param description the text describing the event
     * @param from the event start date
     * @param to the event end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        this(description, from, to, false);
    }

    /**
     * Restores an event with its saved completion state.
     *
     * @param description the text describing the event
     * @param from the event start date
     * @param to the event end date
     * @param isDone whether the event has been completed
     */
    public Event(String description, LocalDate from, LocalDate to, boolean isDone) {
        this(description, from, to, null, null, isDone);
    }

    /**
     * Creates an incomplete single-day event with a start and end time.
     *
     * @param description the text describing the event
     * @param date the date on which the event occurs
     * @param startTime the event start time
     * @param endTime the event end time
     */
    public Event(String description, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this(description, date, startTime, endTime, false);
    }

    /**
     * Restores a timed single-day event with its saved completion state.
     *
     * @param description the text describing the event
     * @param date the date on which the event occurs
     * @param startTime the event start time
     * @param endTime the event end time
     * @param isDone whether the event has been completed
     */
    public Event(String description, LocalDate date, LocalTime startTime, LocalTime endTime, boolean isDone) {
        this(description, date, date, startTime, endTime, isDone);
    }

    /**
     * Creates an event after validating its dates and optional time range.
     */
    private Event(String description, LocalDate from, LocalDate to,
                  LocalTime startTime, LocalTime endTime, boolean isDone) {
        super(description, isDone);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Event dates cannot be null.");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("An event's end date cannot be before its start date.");
        }
        if ((startTime == null) != (endTime == null)) {
            throw new IllegalArgumentException("An event requires both a start and an end time.");
        }
        if (startTime != null && !from.equals(to)) {
            throw new IllegalArgumentException("A timed event must occur on one date.");
        }
        if (startTime != null && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("An event's end time must be after its start time.");
        }
        this.from = from;
        this.to = to;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns whether another task is an event with the same description and date range.
     *
     * @param other the task to compare
     * @return {@code true} if both events have the same details
     */
    @Override
    boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && from.equals(((Event) other).from)
                && to.equals(((Event) other).to)
                && java.util.Objects.equals(startTime, ((Event) other).startTime)
                && java.util.Objects.equals(endTime, ((Event) other).endTime);
    }

    /**
     * Returns whether the date falls within this event's inclusive date range.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    /**
     * Returns this event's start date.
     *
     * @return the event start date
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns this event's end date.
     *
     * @return the event end date
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns the start time of a timed event.
     *
     * @return the start time, or an empty value for an all-day event
     */
    public Optional<LocalTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    /**
     * Returns the end time of a timed event.
     *
     * @return the end time, or an empty value for an all-day event
     */
    public Optional<LocalTime> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    /**
     * Returns a display-ready representation of this event.
     *
     * @return the event type marker, completion status, description, and formatted date range.
     */
    @Override
    public String toString() {
        if (startTime != null) {
            return "[E]" + super.toString() + " (on: " + from.format(DISPLAY_DATE_FORMAT)
                    + ", " + startTime.format(DISPLAY_TIME_FORMAT)
                    + " to " + endTime.format(DISPLAY_TIME_FORMAT) + ")";
        }
        if (from.equals(to)) {
            return "[E]" + super.toString() + " (on: " + from.format(DISPLAY_DATE_FORMAT) + ")";
        }
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
