package yapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** Due date and time, without a time zone. */
    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description Description of the task.
     * @param by Date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns this deadline as a pipe-separated storage record.
     *
     * @return Task type, completion state, description, and due date and time.
     */
    @Override
    public String toDataString() {
        return "D | " + (isDone ? "1" : "0") + " | " + description
                + " | " + TaskDateTime.formatForStorage(by);
    }

    /**
     * Returns whether the deadline is due on the given calendar date.
     *
     * @param date Calendar date to check.
     * @return Whether the date matches, ignoring time of day.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /**
     * Returns this deadline in the format used by the chatbot.
     *
     * @return Type marker, completion status, and description with its scheduled date or times.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + TaskDateTime.formatForDisplay(by) + ")";
    }
}
