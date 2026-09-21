package quackie.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH);
    private static final String ISO_DATE_PATTERN = "\\d{4}-\\d{1,2}-\\d{1,2}";
    private static final String DATE_TIME_PATTERN = "\\d{1,2}/\\d{1,2}/\\d{4} \\d{4}";

    private final String by;
    private final LocalDate date;
    private final LocalDateTime dateTime;

    /**
     * Creates a not-done deadline task.
     *
     * @param description the text describing the task
     * @param by the date or time by which the task should be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
        this.dateTime = parseDateTime(by);
        this.date = parseDate(by);
        if (isStructuredDate(by) && dateTime == null && date == null) {
            throw new IllegalArgumentException("Invalid deadline date or time");
        }
    }

    @Override
    protected String getTypeIcon() {
        return "[D]";
    }

    /**
     * Returns the date or time by which this task should be completed.
     *
     * @return the deadline value
     */
    public String getBy() {
        return by;
    }

    /**
     * Returns the parsed deadline date when the input uses ISO date format.
     *
     * @return the parsed date, or {@code null} for a free-form deadline value
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the parsed deadline date and time when the input uses the supported format.
     *
     * @return the parsed date and time, or {@code null} for a date-only or free-form value
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Parses the supported day-month-year and 24-hour time format.
     *
     * @param value the deadline value entered by the user
     * @return the parsed date and time, or {@code null} when the value is not in that format
     */
    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /**
     * Identifies values that are intended to use one of Quackie's structured date formats.
     *
     * @param value the deadline value entered by the user
     * @return {@code true} when the value resembles an ISO date or date-time value
     */
    private boolean isStructuredDate(String value) {
        return value.matches(ISO_DATE_PATTERN) || value.matches(DATE_TIME_PATTERN);
    }

    /**
     * Parses the supported ISO date format while preserving older free-form values.
     *
     * @param value the deadline value entered by the user
     * @return the parsed date, or {@code null} when the value is not an ISO date
     */
    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /**
     * Formats a parsed date for display, or returns the original free-form value.
     *
     * @return the user-facing deadline value
     */
    private String getDisplayBy() {
        if (dateTime != null) {
            return DISPLAY_DATE_TIME_FORMAT.format(dateTime);
        }
        return date == null ? by : DISPLAY_DATE_FORMAT.format(date);
    }

    @Override
    boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && by.equalsIgnoreCase(((Deadline) other).by);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + getDisplayBy() + ")";
    }
}
