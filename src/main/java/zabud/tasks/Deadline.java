package zabud.tasks;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    /**
     * Deadline date, or {@code null} for a time-only deadline.
     */
    private final LocalDate date;
    /**
     * Deadline time, or {@code null} for a date-only deadline.
     */
    private final LocalTime time;

    /**
     * Creates a deadline task.
     *
     * @param description the text describing the task.
     * @param date deadline date, or {@code null} for a time-only deadline.
     * @param time deadline time, or {@code null} for a date-only deadline.
     */
    public Deadline(String description, LocalDate date, LocalTime time) {
        super(description);
        this.date = date;
        this.time = time;
    }

    /**
     * Returns the deadline date.
     *
     * @return deadline date, or {@code null} for a time-only deadline.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the deadline time.
     *
     * @return deadline time, or {@code null} for a date-only deadline.
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getStorageDetails() {
        return new String[] {date == null ? "" : date.toString(), time == null ? "" : time.toString()};
    }

    /**
     * Returns the Duke code for deadline tasks.
     *
     * @return {@code "D"}.
     */
    @Override
    protected String getTaskType() {
        return "D";
    }

    /**
     * Returns the deadline time as list details.
     *
     * @return the deadline details formatted for the task list.
     */
    @Override
    protected String getAdditionalDetails() {
        return " (by: " + (date == null ? time : time == null ? date : date + " " + time) + ")";
    }
}
