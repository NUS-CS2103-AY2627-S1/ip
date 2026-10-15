package peanutbuttercat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a task that must be completed by a calendar date and time. */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mm a");
    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline with the given description and due time.
     *
     * @param description Description of the deadline.
     * @param by Date and time by which the deadline should be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        if (by == null) {
            throw new IllegalArgumentException("Task due time cannot be blank.");
        }
        this.by = by;
    }

    /**
     * Returns the date and time by which this deadline should be completed.
     *
     * @return This deadline's due date and time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns whether another deadline has the same description and due time. */
    @Override
    boolean hasSameDetails(Task other) {
        if (!(other instanceof Deadline otherDeadline)) {
            return false;
        }
        return super.hasSameDetails(other) && by.equals(otherDeadline.by);
    }

    /**
     * Returns whether this deadline is due on the supplied date.
     *
     * @param date Date to compare with the deadline's due date.
     * @return {@code true} if the deadline is due on the date, otherwise {@code false}.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /**
     * Returns the deadline in the format shown to users.
     *
     * @return A display string containing the task status, description, and due time.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(OUTPUT_FORMAT) + ")";
    }

    /**
     * Returns the deadline in the format used by the storage file.
     *
     * @return A pipe-delimited deadline record.
     */
    @Override
    public String toFileString() {
        return appendCompletionDate("D | " + getCompletionState() + " | "
                + escapeStorageField(getDescription()) + " | " + by);
    }
}
