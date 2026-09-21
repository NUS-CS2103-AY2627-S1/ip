package aster.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter STORAGE_DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/M/yyyy HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));

    private final LocalDateTime byDateTime;
    private final String byText;
    private final boolean hasTime;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description text describing the task
     * @param by date or time by which the task must be completed
     */
    public Deadline(String description, String by) {
        super(description);
        ParsedDeadline parsed = parseDeadline(by);
        this.byDateTime = parsed == null ? null : parsed.dateTime;
        this.byText = by;
        this.hasTime = parsed != null && parsed.hasTime;
    }

    /**
     * Returns the due date represented by this deadline when it is parseable as a date.
     *
     * @return the due date, or {@code null} when the deadline is stored as freeform text
     */
    public LocalDate getByDate() {
        return byDateTime == null ? null : byDateTime.toLocalDate();
    }

    /**
     * Returns the due date and time represented by this deadline when it is parseable.
     *
     * @return the due date-time, or {@code null} when the deadline is stored as freeform text
     */
    public LocalDateTime getByDateTime() {
        return byDateTime;
    }

    /**
     * Returns the raw due-date text entered for this deadline.
     *
     * @return raw due-date text
     */
    public String getByText() {
        return byText;
    }

    /**
     * Returns this deadline in the command-line display format.
     *
     * @return deadline type marker, status marker, description, and due text
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatDueText() + ")";
    }

    /**
     * Returns this deadline in the persistent storage format.
     *
     * @return storage line containing task type, status, description, and due text
     */
    @Override
    public String toStorageString() {
        String storedValue = byDateTime == null ? byText : formatStoredDate(byDateTime);
        return "D | " + (isDone() ? "1" : "0") + " | " + getDescription() + " | " + storedValue
                + getStorageTagSuffix();
    }

    /**
     * Checks whether the given text matches one of the supported date or date-time formats.
     *
     * @param text raw due-date text
     * @return {@code true} when the text is parseable as a date or date-time
     */
    public static boolean isParseable(String text) {
        return parseDeadline(text) != null;
    }

    /**
     * Attempts to parse the given text as a date or date-time in one of the supported formats.
     *
     * @param text raw due-date text
     * @return parsed deadline, or {@code null} when the text does not match any supported format
     */
    private static ParsedDeadline parseDeadline(String text) {
        if (text == null) {
            return null;
        }

        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime parsedTime = LocalDateTime.parse(trimmed, formatter);
                return new ParsedDeadline(parsedTime, true);
            } catch (DateTimeParseException ignored) {
                // fall through to next format
            }
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDate parsedDate = LocalDate.parse(trimmed, formatter);
                return new ParsedDeadline(parsedDate.atStartOfDay(), false);
            } catch (DateTimeParseException ignored) {
                // fall through to next format
            }
        }

        return null;
    }

    /**
     * Formats the due date or date-time for command-line display.
     *
     * @return display text for the due date or date-time
     */
    private String formatDueText() {
        if (byDateTime == null) {
            return byText;
        }
        if (hasTime) {
            return byDateTime.format(DISPLAY_DATE_TIME_FORMAT);
        }
        return byDateTime.toLocalDate().format(DISPLAY_DATE_FORMAT);
    }

    /**
     * Formats a due date or date-time for persistent storage.
     *
     * @param dateTime due date-time to format
     * @return storage text for the due date or date-time
     */
    private String formatStoredDate(LocalDateTime dateTime) {
        if (hasTime) {
            return dateTime.format(STORAGE_DATE_TIME_FORMAT);
        }
        return dateTime.toLocalDate().format(STORAGE_DATE_FORMAT);
    }

    private static final class ParsedDeadline {
        private final LocalDateTime dateTime;
        private final boolean hasTime;

        private ParsedDeadline(LocalDateTime dateTime, boolean hasTime) {
            this.dateTime = dateTime;
            this.hasTime = hasTime;
        }
    }
}
