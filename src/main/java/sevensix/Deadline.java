package sevensix;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The calendar date by which the task should be completed. */
    private final LocalDate by;
    /** The optional clock time by which the task should be completed. */
    private final LocalTime byTime;

    /**
     * Creates a deadline task that is initially not done.
     *
     * @param description the text describing the task.
     * @param by the due date.
     */
    public Deadline(String description, LocalDate by) {
        this(description, by, null);
    }

    /**
     * Creates a deadline task with a date and time.
     *
     * @param description the text describing the task.
     * @param by the due date and time.
     */
    public Deadline(String description, LocalDateTime by) {
        this(description, by.toLocalDate(), by.toLocalTime());
    }

    /**
     * Creates a deadline task with a date and optional time.
     *
     * @param description the text describing the task.
     * @param by the due date.
     * @param byTime the optional due time.
     */
    public Deadline(String description, LocalDate by, LocalTime byTime) {
        super(description, TaskType.DEADLINE);
        assert by != null : "A deadline always has a due date; only its due time is optional";
        this.by = by;
        this.byTime = byTime;
    }

    /**
     * Returns the deadline details for persistence.
     *
     * @return the due date.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns the deadline time for persistence.
     *
     * @return the due time, or {@code null} when only a date was supplied.
     */
    public LocalTime getByTime() {
        return byTime;
    }

    /**
     * Returns the deadline task with its due date or time.
     *
     * @return the formatted deadline task.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimeParser.formatForDisplay(by, byTime) + ")";
    }
}
