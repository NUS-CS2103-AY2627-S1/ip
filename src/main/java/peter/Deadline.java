package peter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task that should be completed by a specified date and time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmm");

    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the deadline description
     * @param by the deadline date and time
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline date and time.
     *
     * @return the deadline date and time
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns this deadline in the format used by Peter's storage file.
     *
     * @return the deadline's pipe-delimited storage representation
     */
    @Override
    public String toFileFormat() {
        return "D | " + (isDone ? DONE_MARKER : NOT_DONE_MARKER) + " | " + description + " | "
                + by.format(STORAGE_DATE_FORMAT);
    }

    /**
     * Returns this deadline in Peter's normal display format.
     *
     * @return the formatted deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatDateTime(by) + ")";
    }

    /**
     * Formats a date and time for display without showing midnight unnecessarily.
     *
     * @param dateTime the date and time to format
     * @return the formatted date, with time when it is not midnight
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(OUTPUT_DATE_FORMAT);
        }
        return dateTime.format(OUTPUT_DATE_TIME_FORMAT);
    }

    /**
     * Compares deadlines by description and due time, per {@link Task#equals}.
     *
     * @param other the object to compare against
     * @return true if {@code other} is a deadline with an equal description and due time
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        Deadline otherDeadline = (Deadline) other;
        return by.equals(otherDeadline.by);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), by);
    }
}
