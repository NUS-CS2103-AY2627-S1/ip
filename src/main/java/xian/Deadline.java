package xian;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Represents a task that needs to be completed before a specific date and time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private LocalDateTime byDate;

    /**
     * Creates a new Deadline task with the given description and due date/time.
     *
     * @param description the description of the task.
     * @param byDate The date and time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime byDate) {
        super(description);
        this.byDate = byDate;
    }

    /**
     * Returns the date and time by which this task should be completed.
     *
     * @return the deadline date and time.
     */
    public LocalDateTime getByDate() {
        return this.byDate;
    }

    /**
     * Updates the date and time by which this task should be completed.
     *
     * @param byDate the new deadline date and time.
     */
    public void setByDate(LocalDateTime byDate) {
        this.byDate = byDate;
    }

    /**
     * Returns the string representation of this Deadline task,
     * prefixed with "[D]" and including its due date and time.
     *
     * @return the formatted string representation of this task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + byDate.format(DATE_TIME_FORMAT) + ")";
    }
}
