package potato.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents an event task with start and end times or dates.
 */
public class Event extends Task {
    protected String from;
    protected String to;
    protected LocalDate fromDate;
    protected LocalDate toDate;
    protected LocalDateTime fromDateTime;
    protected LocalDateTime toDateTime;

    private static final DateTimeFormatter[] DATETIME_FORMATTERS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    };
    private static final DateTimeFormatter OUTPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter OUTPUT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm");

    /**
     * Constructs an {@code Event} task with a description, start time, and end time.
     *
     * @param description Description of the event.
     * @param from Start time or date string.
     * @param to End time or date string.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;

        ParsedTimeResult startResult = parseDateOrDateTime(from);
        this.fromDate = startResult.date;
        this.fromDateTime = startResult.dateTime;

        ParsedTimeResult endResult = parseDateOrDateTime(to);
        this.toDate = endResult.date;
        this.toDateTime = endResult.dateTime;
    }

    private ParsedTimeResult parseDateOrDateTime(String input) {
        for (DateTimeFormatter formatter : DATETIME_FORMATTERS) {
            try {
                return new ParsedTimeResult(null, LocalDateTime.parse(input, formatter));
            } catch (DateTimeParseException ignored) {
                // Try next pattern
            }
        }
        try {
            return new ParsedTimeResult(LocalDate.parse(input), null);
        } catch (DateTimeParseException e) {
            return new ParsedTimeResult(null, null);
        }
    }

    private static class ParsedTimeResult {
        final LocalDate date;
        final LocalDateTime dateTime;

        ParsedTimeResult(LocalDate date, LocalDateTime dateTime) {
            this.date = date;
            this.dateTime = dateTime;
        }
    }

    /**
     * Returns the raw start time or date string.
     *
     * @return Raw start string.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the raw end time or date string.
     *
     * @return Raw end string.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the string representation of the event task.
     *
     * @return Formatted event display string.
     */
    @Override
    public String toString() {
        String displayFrom = formatTimeOrDate(fromDateTime, fromDate, from);
        String displayTo = formatTimeOrDate(toDateTime, toDate, to);
        return "[E]" + super.toString() + " (from: " + displayFrom + " to: " + displayTo + ")";
    }

    private String formatTimeOrDate(LocalDateTime dt, LocalDate d, String raw) {
        if (dt != null) {
            return dt.format(OUTPUT_DATETIME_FORMATTER);
        } else if (d != null) {
            return d.format(OUTPUT_DATE_FORMATTER);
        }
        return raw;
    }
}