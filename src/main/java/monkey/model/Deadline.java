package monkey.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Represents a task that must be completed by a typed date or date/time. */
public class Deadline extends Task {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private LocalDate date;
    private LocalDateTime dateTime;

    /** Creates an incomplete deadline with its description and due date/time. */
    public Deadline(String description, String by) {
        super(description);
        setDeadline(by);
    }

    /** Reschedules this deadline to the supplied date or date-time. */
    public void reschedule(String by) {
        setDeadline(by);
    }

    /** Returns whether this deadline is later than the current date and time. */
    public boolean isInFuture() {
        return date != null
                ? date.isAfter(LocalDate.now())
                : dateTime.isAfter(LocalDateTime.now());
    }

    private void setDeadline(String by) {
        String value = by == null ? "" : by.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("A deadline needs a valid date or time.");
        }
        LocalDate parsedDate = null;
        LocalDateTime parsedDateTime = null;
        try {
            parsedDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException dateError) {
            try {
                parsedDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("d/M/uuuu"));
            } catch (DateTimeParseException slashDateError) {
                parsedDateTime = parseDateTime(value);
            }
        }
        date = parsedDate;
        dateTime = parsedDateTime;
        // Exactly one representation is expected after parsing the supported date formats.
        assert (date != null) ^ (dateTime != null) : "Deadline must contain one date representation";
    }

    /** Parses the supported date/time formats used by deadline input. */
    private static LocalDateTime parseDateTime(String value) {
        DateTimeFormatter[] formats = {
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm"),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
        };
        for (DateTimeFormatter format : formats) {
            try {
                return LocalDateTime.parse(value, format);
            } catch (DateTimeParseException ignored) {
                // Try the next supported input format.
            }
        }
        throw new IllegalArgumentException("Use a date like yyyy-mm-dd or d/M/yyyy, or a date/time like "
                + "d/M/yyyy HHmm.");
    }

    /** Returns the human-readable deadline value for display. */
    public String getBy() {
        return date != null ? date.format(DATE_FORMAT) : dateTime.format(DATE_TIME_FORMAT);
    }

    /** Returns the canonical value used by storage. */
    public String getStorageValue() {
        return date != null ? date.toString() : dateTime.format(DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm"));
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && getStorageValue().equals(((Deadline) other).getStorageValue());
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + getBy() + ")";
    }
}
