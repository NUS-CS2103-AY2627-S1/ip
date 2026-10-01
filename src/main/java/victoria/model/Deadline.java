package victoria.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import victoria.exception.InvalidDeadlineException;

/** A task that must be completed by a supplied date. */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private final LocalDate by;

    /** Creates an unfinished deadline. */
    public Deadline(String description, String by) {
        this(description, parseDate(by));
    }

    /** Creates an unfinished deadline at the supplied date. */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /** Returns the deadline as a machine-readable value for persistence. */
    public String getBy() { return by.toString(); }

    /** Returns the date on which this deadline occurs. */
    public LocalDate getDate() { return by; }

    /** Parses an ISO date or throws a user-facing deadline error. */
    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("uuuu-MM-dd"));
        } catch (DateTimeParseException | NullPointerException exception) {
            throw new InvalidDeadlineException("Please use yyyy-MM-dd for the deadline date, for example 2026-09-12.");
        }
    }

    /** Returns the marker identifying this task as a deadline. */
    @Override
    protected String getTypeMarker() {
        return "[D]";
    }

    /** Returns the formatted deadline text displayed after the description. */
    @Override
    protected String getTimingText() {
        return " (by: " + by.format(OUTPUT_FORMAT) + ")";
    }
}
