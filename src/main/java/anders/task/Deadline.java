package anders.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/** Represents a task that must be completed by a specified time. */
public class Deadline extends Task {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter ISO_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter ISO_COLON_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            DATE_TIME_FORMAT, ISO_DATE_TIME_FORMAT, ISO_COLON_DATE_TIME_FORMAT);
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(DATE_FORMAT, DateTimeFormatter.ISO_LOCAL_DATE);
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT = DateTimeFormatter.ofPattern("h.mm a", Locale.ENGLISH);
    private final LocalDateTime by;
    private final boolean hasTime;
    private final DateTimeFormatter persistenceFormat;

    /**
     * Creates a new unfinished deadline task.
     *
     * @param description the task description
     * @param by the deadline in d/M/yyyy or yyyy-MM-dd format, optionally followed by HHmm;
     *           yyyy-MM-dd also accepts HH:mm
     * @throws DateTimeParseException if the deadline is not a valid supported date or date/time
     */
    public Deadline(String description, String by) {
        super(description);
        ParsedDeadline parsed = parseDeadline(by);
        this.by = parsed.value();
        this.hasTime = parsed.hasTime();
        this.persistenceFormat = parsed.format();
        assert this.by != null : "A deadline must have a parsed date";
        assert this.hasTime == DATE_TIME_FORMATS.contains(this.persistenceFormat)
                : "Deadline time metadata must match its persistence format";
    }

    /** Returns the typed deadline date. */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns the display text for this deadline task. */
    @Override
    public String toString() {
        String time = by.format(DISPLAY_TIME_FORMAT).toLowerCase(Locale.ROOT);
        String display = by.format(DISPLAY_FORMAT) + (hasTime ? " " + time : "");
        return "[D] " + super.toString() + " (by: " + display + ")" + getTagsDisplayText();
    }

    /** Returns the canonical value used for persistence. */
    public String getByText() {
        return by.format(persistenceFormat);
    }

    /** Parses supported formats while retaining whether a time was supplied and how to save it. */
    private static ParsedDeadline parseDeadline(String value) {
        DateTimeParseException lastError = new DateTimeParseException("Invalid deadline date/time", value, 0);
        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                return new ParsedDeadline(LocalDateTime.parse(value, format), true, format);
            } catch (DateTimeParseException exception) {
                lastError = exception;
            }
        }
        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return new ParsedDeadline(LocalDate.parse(value, format).atStartOfDay(), false, format);
            } catch (DateTimeParseException exception) {
                lastError = exception;
            }
        }
        throw lastError;
    }

    /** Stores a deadline's value, time precision, and matching persistence format. */
    private record ParsedDeadline(LocalDateTime value, boolean hasTime, DateTimeFormatter format) {
    }

}
