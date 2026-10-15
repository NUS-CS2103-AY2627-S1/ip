package bo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import bo.time.DateTimeParser;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The parsed date/time by which this task should be completed. */
    private final LocalDateTime by;

    /** Whether the original value included a time. */
    private final boolean includesTime;

    /** The original free-form value, used for backwards-compatible old data. */
    private final String legacyBy;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the task.
     * @param by the date or time by which the task should be completed.
     */
    public Deadline(String description, String by) {
        super(description);
        if (by == null || by.isBlank()) {
            throw new IllegalArgumentException("A deadline must have a non-blank date.");
        }
        DateTimeParser.ParsedDateTime parsed = tryParse(by);
        this.by = parsed == null ? null : parsed.value();
        this.includesTime = parsed != null && parsed.includesTime();
        this.legacyBy = parsed == null ? by : null;
    }

    /**
     * Creates an incomplete deadline with a date-only value.
     *
     * @param description the text describing the task.
     * @param by the date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by.atStartOfDay();
        this.includesTime = false;
        this.legacyBy = null;
    }

    /**
     * Creates an incomplete deadline with a date and time.
     *
     * @param description the text describing the task.
     * @param by the date and time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
        this.includesTime = true;
        this.legacyBy = null;
    }

    /**
     * Returns the parsed deadline value.
     *
     * @return the deadline as a {@link LocalDateTime}, or {@code null} for an
     *         old free-form value that could not be parsed.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns the value used when displaying this task.
     *
     * @return a formatted date/time, or the old free-form value.
     */
    public String getDisplayBy() {
        if (legacyBy != null) {
            return legacyBy;
        }
        return DateTimeParser.format(new DateTimeParser.ParsedDateTime(by, includesTime));
    }

    /**
     * Returns the value used in the storage file.
     *
     * @return an ISO date/time, or the old free-form value.
     */
    public String getStoredBy() {
        if (legacyBy != null) {
            return legacyBy;
        }
        return DateTimeParser.serialize(new DateTimeParser.ParsedDateTime(by, includesTime));
    }

    /**
     * Returns the icon used for deadline tasks.
     *
     * @return {@code D}.
     */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns the task including its deadline.
     *
     * @return the formatted deadline task.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + getDisplayBy() + ")";
    }

    /**
     * Tries to parse a value while retaining old free-form values from earlier
     * versions of Bo.
     *
     * @param value the value to parse.
     * @return the parsed value, or {@code null} when it is legacy text.
     */
    private static DateTimeParser.ParsedDateTime tryParse(String value) {
        try {
            return DateTimeParser.parse(value);
        } catch (IllegalArgumentException exception) {
            if (DateTimeParser.looksLikeStructuredDateTime(value)) {
                throw exception;
            }
            return null;
        }
    }
}
