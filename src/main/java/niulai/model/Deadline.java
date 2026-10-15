package niulai.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/**
 * Represents a task that must be completed before a specified date or time.
 */
public class Deadline extends Task {
    /** Formats date-only deadlines for display. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /** Formats date-and-time deadlines for display. */
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH);

    /** Formats date-and-time deadlines for storage. */
    private static final DateTimeFormatter STORAGE_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH);

    /** The date by which the task should be completed, if it has no time. */
    private final LocalDate byDate;

    /** The date and time by which the task should be completed, if present. */
    private final LocalDateTime byDateTime;

    /** The original free-form value for backwards-compatible non-date deadlines. */
    private final String byText;

    /**
     * Creates a new incomplete deadline task.
     *
     * @param description the text describing the task
     * @param by the date or time by which the task should be completed
     */
    public Deadline(String description, String by) {
        super(description);

        if (by == null || by.isBlank()) {
            throw new IllegalArgumentException("Deadline time cannot be blank.");
        }

        String value = normalizeField(by);
        TemporalValue.parse(value);
        LocalDateTime parsedDateTime = parseDateTimeValue(value);
        LocalDate parsedDate = parsedDateTime == null ? parseDateValue(value) : null;

        this.byDate = parsedDate;
        this.byDateTime = parsedDateTime;
        this.byText = parsedDate == null && parsedDateTime == null ? value : null;
        assert hasExactlyOneValueRepresentation()
                : "A deadline must have exactly one value representation.";
    }

    /**
     * Creates a deadline backed by a date value.
     *
     * @param description the text describing the task
     * @param by the date by which the task should be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description);

        if (by == null) {
            throw new IllegalArgumentException("Deadline date cannot be null.");
        }

        this.byDate = by;
        this.byDateTime = null;
        this.byText = null;
        assert hasExactlyOneValueRepresentation()
                : "A deadline must have exactly one value representation.";
    }

    /**
     * Creates a deadline backed by a date-and-time value.
     *
     * @param description the text describing the task
     * @param by the date and time by which the task should be completed
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);

        if (by == null) {
            throw new IllegalArgumentException("Deadline date and time cannot be null.");
        }

        this.byDate = null;
        this.byDateTime = by;
        this.byText = null;
        assert hasExactlyOneValueRepresentation()
                : "A deadline must have exactly one value representation.";
    }

    /** @return the deadline task type */
    @Override
    public TaskType getType() {
        return TaskType.DEADLINE;
    }

    /**
     * Returns the escaped storage representation of this deadline.
     *
     * @return the base task fields followed by the deadline value
     */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + escapeStorageField(getStorageValue());
    }

    /**
     * Returns the display representation of this deadline.
     *
     * @return the base task representation followed by the deadline value
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + getDisplayValue() + ")";
    }

    /**
     * Returns the date-only value represented by this deadline.
     *
     * @return the deadline date, or {@code null} when it has a time or is free-form text
     */
    public LocalDate getByDate() {
        return byDate;
    }

    /**
     * Returns the date-and-time value represented by this deadline.
     *
     * @return the deadline date and time, or {@code null} when it has no time or is free-form text
     */
    public LocalDateTime getByDateTime() {
        return byDateTime;
    }

    /** Returns the description and deadline value used for duplicate detection. */
    @Override
    protected List<String> getIdentityFields() {
        return List.of(
                normalizeIdentityValue(getDescription()),
                normalizeIdentityValue(getStorageValue())
        );
    }

    /**
     * Checks whether this deadline falls on the specified date.
     *
     * @param date the date to check
     * @return whether this deadline occurs on the date
     */
    public boolean occursOn(LocalDate date) {
        if (date == null) {
            return false;
        }

        if (byDateTime != null) {
            return byDateTime.toLocalDate().equals(date);
        }

        return byDate != null && byDate.equals(date);
    }

    /** Returns the value used for display. */
    private String getDisplayValue() {
        if (byDateTime != null) {
            return byDateTime.format(DISPLAY_DATE_TIME_FORMATTER);
        }

        if (byDate != null) {
            return byDate.format(DISPLAY_DATE_FORMATTER);
        }

        return byText;
    }

    /** Returns a stable, parseable value for saving to disk. */
    private String getStorageValue() {
        if (byDateTime != null) {
            return byDateTime.format(STORAGE_DATE_TIME_FORMATTER);
        }

        if (byDate != null) {
            return byDate.toString();
        }

        return byText;
    }

    /** @return whether exactly one internal deadline-value representation is set */
    private boolean hasExactlyOneValueRepresentation() {
        int representationCount = 0;
        if (byDate != null) {
            representationCount++;
        }
        if (byDateTime != null) {
            representationCount++;
        }
        if (byText != null) {
            representationCount++;
        }
        return representationCount == 1;
    }

    /** Tries the supported date-and-time formats. */
    static LocalDateTime parseDateTimeValue(String value) {
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm", Locale.ENGLISH)
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(value, formatter.withResolverStyle(ResolverStyle.STRICT));
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        return null;
    }

    /** Tries the supported date-only formats. */
    static LocalDate parseDateValue(String value) {
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("d/M/uuuu", Locale.ENGLISH)
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(value, formatter.withResolverStyle(ResolverStyle.STRICT));
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        return null;
    }
}
