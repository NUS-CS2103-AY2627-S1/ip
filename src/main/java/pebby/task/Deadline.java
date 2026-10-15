package pebby.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/** Represents a task that must be completed by a supplied deadline. */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter WRITTEN_DATE_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMMM uuuu")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    private final LocalDate by;

    public Deadline(String description, String by) {
        this(description, parseDate(by));
    }

    /** Creates a deadline using an already validated date. */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Converts an ISO or written English date argument into a LocalDate.
     *
     * @throws IllegalArgumentException if the argument is not a real date in an accepted format
     */
    public static LocalDate parseDate(String dateText) {
        if (dateText == null || dateText.isBlank()) {
            throw invalidDateException();
        }
        try {
            return LocalDate.parse(dateText.trim());
        } catch (DateTimeParseException exception) {
            try {
                return LocalDate.parse(dateText.trim(), WRITTEN_DATE_FORMAT);
            } catch (DateTimeParseException writtenDateException) {
                throw invalidDateException();
            }
        }
    }

    /** Returns the shared user-facing explanation for an invalid date. */
    private static IllegalArgumentException invalidDateException() {
        return new IllegalArgumentException("Please use a valid date as yyyy-MM-dd or d MMMM yyyy, "
                + "for example 2019-12-02 or 15 June 2026.");
    }

    /** Returns the deadline in ISO format so it can be saved and loaded reliably. */
    public String getBy() {
        return by.toString();
    }

    /** Returns whether this deadline falls on the specified date. */
    public boolean isOn(LocalDate date) {
        return by.equals(date);
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && by.equals(((Deadline) other).by);
    }

    @Override
    public String toString() {
        return "[D] " + super.toString() + " (by: " + by.format(OUTPUT_DATE_FORMAT) + ")";
    }
}
