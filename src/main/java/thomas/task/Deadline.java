package thomas.task;

import java.time.LocalDate;
import java.time.LocalTime;

import thomas.ThomasException;
import thomas.util.DateTimeUtil;

/**
 * Represents a task with a deadline to be completed by.
 */
public class Deadline extends Task {
    protected LocalDate byDate;
    protected LocalTime byTime;

    /**
     * Creates a deadline task with the given description and due date/time.
     *
     * @param description Textual description of the task.
     * @param by Target date or time for completion.
     * @throws ThomasException If the date string format is invalid.
     */
    public Deadline(String description, String by) throws ThomasException {
        super(description);
        DateTimeUtil.ParsedDateTime parsed = DateTimeUtil.parseDateTime(by);
        this.byDate = parsed.getDate();
        this.byTime = parsed.getTime();
        assert byDate != null : "A parsed deadline must have a date";
    }

    public LocalDate getByDate() {
        return this.byDate;
    }

    /**
     * Gets the optional due time of this deadline.
     *
     * @return the due time, or {@code null} if only a date was given
     */
    public LocalTime getByTime() {
        return this.byTime;
    }

    /**
     * Returns a formatted string for file storage.
     *
     * @return Formatted string representing this deadline task.
     */
    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + DateTimeUtil.toStorageString(byDate, byTime);
    }

    /**
     * Checks whether this deadline falls on the given date.
     *
     * @param date date to compare against
     * @return {@code true} if the due date matches
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return byDate.equals(date);
    }

    /**
     * Returns the status, description, and due date/time of this deadline.
     *
     * @return Status icon, description, and due date/time as a string.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeUtil.toDisplayString(byDate, byTime) + ")";
    }
}
