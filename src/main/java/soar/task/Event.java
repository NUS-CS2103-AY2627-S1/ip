package soar.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import soar.parser.DateTimeParser;
import soar.storage.Storage;

/**
 * Represents a task that takes place between a start and end date or time.
 */
public class Event extends Task {
    /** Date or time at which the event starts. */
    private final String from;

    /** Date or time at which the event ends. */
    private final String to;

    /**
     * Creates an incomplete event with the given description and time range.
     *
     * @param description Description of the event.
     * @param from Date or time at which the event starts.
     * @param to Date or time at which the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start value.
     *
     * @return free-form start value
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's end value.
     *
     * @return free-form end value
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns whether dated event boundaries are in chronological order.
     *
     * <p>Free-form times remain valid because Soar cannot reliably compare them.
     * When both values include supported date-times, their times are compared;
     * otherwise, supported calendar dates are compared.</p>
     *
     * @param from Event start value.
     * @param to Event end value.
     * @return {@code true} if the known boundaries are in chronological order
     */
    public static boolean hasValidDateOrder(String from, String to) {
        Optional<LocalDateTime> startDateTime = DateTimeParser.parseDateTime(from);
        Optional<LocalDateTime> endDateTime = DateTimeParser.parseDateTime(to);
        if (startDateTime.isPresent() && endDateTime.isPresent()) {
            return !endDateTime.get().isBefore(startDateTime.get());
        }

        Optional<LocalDate> startDate = DateTimeParser.parseCalendarDate(from);
        Optional<LocalDate> endDate = DateTimeParser.parseCalendarDate(to);
        return startDate.isEmpty() || endDate.isEmpty() || !endDate.get().isBefore(startDate.get());
    }

    /**
     * Returns whether this event occurs on a calendar date.
     *
     * <p>A fully dated start and end form an inclusive range. If only one end
     * contains a supported calendar date, that date alone is matched. This keeps
     * older free-form event times valid while making dated events searchable.</p>
     *
     * @param date Calendar date to check.
     * @return {@code true} if the event occurs on that date
     */
    public boolean occursOn(LocalDate date) {
        Optional<LocalDate> startDate = DateTimeParser.parseCalendarDate(from);
        Optional<LocalDate> endDate = DateTimeParser.parseCalendarDate(to);
        if (startDate.isPresent() && endDate.isPresent()) {
            return !date.isBefore(startDate.get()) && !date.isAfter(endDate.get());
        }
        return startDate.map(date::equals).orElse(false)
                || endDate.map(date::equals).orElse(false);
    }

    /**
     * Returns the icon used to identify an event task.
     *
     * @return event type icon
     */
    @Override
    public String getTypeIcon() {
        return "[E]";
    }

    /**
     * Serializes this event with escaped description and range fields.
     *
     * @return escaped event record
     */
    @Override
    public String toDataString() {
        return "E | " + getDataStatus() + " | " + Storage.escapeField(getDescription())
                + " | " + Storage.escapeField(from) + " | " + Storage.escapeField(to);
    }

    /**
     * Formats this event for display with its start and end values.
     *
     * @return task summary followed by the event range
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
