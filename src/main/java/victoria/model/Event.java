package victoria.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import victoria.exception.InvalidEventException;

/** A task with supplied start and end dates. */
public class Event extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private final LocalDate from;
    private final LocalDate to;
    /** Creates an unfinished event. */
    public Event(String description, String from, String to) {
        super(description);
        this.from = parseDate(from);
        this.to = parseDate(to);
    }

    /** Returns the event start text entered by the user. */
    public String getFrom() { return from.toString(); }

    /** Returns the event end text entered by the user. */
    public String getTo() { return to.toString(); }

    /** Returns the event start date. */
    public LocalDate getStartDate() { return from; }

    /** Returns the event end date. */
    public LocalDate getEndDate() { return to; }

    /** Parses an ISO date or throws a user-facing event error. */
    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("uuuu-MM-dd"));
        } catch (DateTimeParseException | NullPointerException exception) {
            throw new InvalidEventException("Please use yyyy-MM-dd for event dates, for example 2026-09-12.");
        }
    }

    /** Returns the marker identifying this task as an event. */
    @Override
    protected String getTypeMarker() {
        return "[E]";
    }

    /** Returns the formatted event range displayed after the description. */
    @Override
    protected String getTimingText() {
        return " (from: " + from.format(OUTPUT_FORMAT) + " to: " + to.format(OUTPUT_FORMAT) + ")";
    }
}
