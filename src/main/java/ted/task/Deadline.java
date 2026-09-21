package ted.task;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A task that must be done before a given point in time,
 * e.g. {@code submit report (by: 2 Dec 2019, 6:00 PM)}.
 */
public class Deadline extends Task {
    /** When the task is due, represented as a date and time. */
    private final LocalDateTime by;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description what the task is.
     * @param by          when the task is due.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /** {@inheritDoc} A deadline is shown as {@code D}. */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * {@inheritDoc} A deadline must also be due at the same time, since the
     * same job due on two different days is two separate deadlines.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other) && other instanceof Deadline deadline && by.equals(deadline.by);
    }

    /** {@inheritDoc} A deadline checks its due time. */
    @Override
    public boolean isAtTimeOfDay(LocalTime time) {
        return by.toLocalTime().equals(time);
    }

    /**
     * Appends the due date and time in ISO format to the shared save-line
     * format, e.g. {@code D | 0 | 2019-12-02T18:00 | return book}.
     */
    @Override
    public String toSaveFormat() {
        return toSaveLine(by.toString());
    }

    /** Appends a user-friendly due date and time to the shared task format. */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }
}
