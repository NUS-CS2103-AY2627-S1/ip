package herta.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;

import herta.parser.DateTimeParser;

/**
 * Represents a task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    private static final String STORAGE_TYPE = "E";
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an incomplete event task.
     *
     * @param description the task description.
     * @param from the event's start date and time.
     * @param to the event's end date and time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = Objects.requireNonNull(from, "Event start date/time cannot be null.");
        this.to = Objects.requireNonNull(to, "Event end date/time cannot be null.");
        DateTimeParser.validateSupportedDateTime(this.from);
        DateTimeParser.validateSupportedDateTime(this.to);
        validateTimeRange(from, to);
    }

    /**
     * Reconstructs an event from its serialized date/time values.
     *
     * @param description the event description.
     * @param fromText the serialized start date/time.
     * @param toText the serialized end date/time.
     * @return the reconstructed event.
     * @throws IllegalArgumentException if a stored date/time or event range is invalid.
     */
    public static Event fromStorage(String description, String fromText, String toText) {
        try {
            LocalDateTime from = DateTimeParser.parseStoredDateTime(fromText);
            LocalDateTime to = DateTimeParser.parseStoredDateTime(toText);
            return new Event(description, from, to);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid saved event date/time: "
                    + e.getParsedString() + ".", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid saved event: end must be after start.", e);
        }
    }

    /**
     * Returns the event's start date and time.
     *
     * @return the event's start date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event's end date and time.
     *
     * @return the event's end date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Checks whether any part of this event overlaps a particular calendar
     * date.
     *
     * @param date the date to check.
     * @return {@code true} if the event occurs during the date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        Objects.requireNonNull(date, "The date to filter cannot be null.");
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.equals(LocalDate.MAX)
                ? LocalDateTime.MAX : date.plusDays(1).atStartOfDay();
        boolean isStartBeforeNextDay = from.isBefore(startOfNextDay);
        boolean isEndAfterStartOfDay = to.isAfter(startOfDay);
        return isStartBeforeNextDay && isEndAfterStartOfDay;
    }

    /**
     * Returns the event start time used for chronological operations and
     * upcoming-event searches.
     *
     * @return the event's start date and time.
     */
    @Override
    public Optional<LocalDateTime> getScheduledDateTime() {
        return Optional.of(from);
    }

    /**
     * Returns this event in the format used by Herta's data file.
     *
     * @return the serialized event text.
     */
    @Override
    public String toStorageString() {
        return STORAGE_TYPE + STORAGE_FIELD_SEPARATOR + getCompletionStatusCode()
                + STORAGE_FIELD_SEPARATOR + getDescription() + STORAGE_FIELD_SEPARATOR
                + DateTimeParser.formatForStorage(from) + STORAGE_FIELD_SEPARATOR
                + DateTimeParser.formatForStorage(to);
    }

    /**
     * Returns this event formatted with its task type, completion status, description,
     * and time range.
     *
     * @return the formatted event text.
     */
    @Override
    public String toString() {
        return "[" + STORAGE_TYPE + "]" + super.toString() + " (from: "
                + DateTimeParser.formatForDisplay(from) + " to: "
                + DateTimeParser.formatForDisplay(to) + ")";
    }

    /**
     * Ensures that an event has a positive duration.
     *
     * @param from the event's start date and time.
     * @param to the event's end date and time.
     * @throws IllegalArgumentException if the end is not after the start.
     */
    private static void validateTimeRange(LocalDateTime from, LocalDateTime to) {
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("Event end must be after its start.");
        }
    }
}
