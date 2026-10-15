package jason.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a task occurring between two date-time values.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm", Locale.ENGLISH);
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    /**
     * Creates an event from ISO date-time or date-only strings.
     *
     * @param description text describing the event.
     * @param startDate event start date and time.
     * @param endDate event end date and time.
     */
    public Event(String description, String startDate, String endDate) {
        super(description);
        this.startDate = parseDate(startDate);
        this.endDate = parseDate(endDate);
    }

    /**
     * Creates an event from typed date-time values.
     *
     * @param description text describing the event.
     * @param startDate event start date and time.
     * @param endDate event end date and time.
     * @throws IllegalArgumentException if either date is null.
     */
    public Event(String description, LocalDateTime startDate, LocalDateTime endDate) {
        super(description);
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Event dates cannot be null.");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the event start date for persistence.
     *
     * @return event start date and time.
     */
    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    /**
     * Returns the event end date for persistence.
     *
     * @return event end date and time.
     */
    public LocalDateTime getEndDate() {
        return this.endDate;
    }

    /**
     * Returns the event formatted for the user interface.
     *
     * @return formatted event description.
     */
    @Override
    public String toString() {
        return String.format("[E] %s (from: %s to: %s)", super.toString(),
                this.startDate.format(DISPLAY_FORMAT), this.endDate.format(DISPLAY_FORMAT));
    }

    /**
     * Parses an ISO date-time, the project input format, or a date-only value.
     */
    private static LocalDateTime parseDate(String date) {
        if (date == null || date.isBlank()) {
            throw new DateTimeParseException("Date cannot be empty", date == null ? "null" : date, 0);
        }
        String value = date.trim();
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(value, INPUT_FORMAT);
            } catch (DateTimeParseException ignored) {
                return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            }
        }
    }

}
