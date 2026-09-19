package benji;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a task that takes place during a specified time period.
 */
public class Event extends Task {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    protected String start;
    protected String end;
    /**
     * Creates a deadline task with a description, start date, and end date.
     * @param description description of the task
     * @param start description of the start date of the task
     * @param end description of the end date of the task
     */
    public Event(String description, String start, String end) {
        super(description);
        assert start != null && !start.isBlank() : "Event should have a start time";
        assert end != null && !end.isBlank() : "Event should have an end time";
        this.start = start;
        this.end = end;
    }

    /**
     * Formats an ISO date for display while preserving free-form event times.
     *
     * @param dateOrTime event date or time text
     * @return formatted date, or the original text when it is not an ISO date
     */
    private String formatDateOrTime(String dateOrTime) {
        try {
            return LocalDate.parse(dateOrTime).format(FORMATTER);
        } catch (DateTimeParseException e) {
            return dateOrTime;
        }
    }

    /**
     * Returns this event with its start and end times in BENJI's display format.
     *
     * @return the event prefixed with "[E]" and followed by its time range
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + formatDateOrTime(start) + " to: "
                + formatDateOrTime(end) + ")";
    }
}
