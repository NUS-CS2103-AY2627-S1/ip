package soar.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import soar.storage.Storage;

/**
 * Represents a task that must be completed by a particular date or time.
 */
public class Deadline extends Task {
    /** Human-readable date format used when showing a deadline. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /** Human-readable time format used when the user supplied a time. */
    private static final DateTimeFormatter DISPLAY_TIME_FORMAT =
            DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);

    /** Date and optional time by which the task should be completed. */
    private final LocalDateTime by;

    /** Whether the original deadline included a time of day. */
    private final boolean hasTime;

    /**
     * Creates an incomplete deadline due on the given date.
     *
     * @param description Description of what needs to be done.
     * @param by Date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by.atStartOfDay();
        this.hasTime = false;
    }

    /**
     * Creates an incomplete deadline due at the given date and time.
     *
     * @param description Description of what needs to be done.
     * @param by Date and time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
        this.hasTime = true;
    }

    /**
     * Returns the deadline as a typed date-time value.
     *
     * <p>Date-only deadlines use midnight and can be distinguished with
     * {@link #hasTime()}.</p>
     *
     * @return due date and time
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns whether this deadline includes an explicit time of day.
     *
     * @return {@code true} when a time was supplied
     */
    public boolean hasTime() {
        return hasTime;
    }

    /**
     * Returns the icon used to identify a deadline task.
     *
     * @return deadline type icon
     */
    @Override
    public String getTypeIcon() {
        return "[D]";
    }

    /**
     * Serializes this deadline while preserving whether a time was supplied.
     *
     * @return escaped deadline record
     */
    @Override
    public String toDataString() {
        String storedBy = hasTime
                ? by.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : by.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return "D | " + getDataStatus() + " | " + Storage.escapeField(getDescription())
                + " | " + storedBy;
    }

    /**
     * Formats this deadline for display using its original date precision.
     *
     * @return task summary followed by the formatted deadline
     */
    @Override
    public String toString() {
        String displayedBy = by.format(DISPLAY_DATE_FORMAT);
        if (hasTime) {
            displayedBy += ", " + by.format(DISPLAY_TIME_FORMAT);
        }
        return super.toString() + " (by: " + displayedBy + ")";
    }
}
