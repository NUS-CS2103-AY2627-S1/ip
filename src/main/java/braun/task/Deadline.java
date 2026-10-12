package braun.task;

import java.time.LocalDate;
import java.util.Objects;

import braun.exception.BraunException;
import braun.util.DateTimeUtil;

/**
 * Represents a deadline task that needs to be done before a specific date/time.
 * Encapsulates date and time using {@link DateTimeUtil.TaskDateTime}.
 */
public class Deadline extends Task {

    /** The structured deadline date/time */
    protected DateTimeUtil.TaskDateTime by;

    /**
     * Constructs a new {@code Deadline} task by parsing the specified date/time string.
     *
     * @param description the textual description of the deadline task.
     * @param by the date/time string by which the task is due.
     * @throws BraunException if the date/time format is invalid.
     */
    public Deadline(String description, String by) throws BraunException {
        super(description);
        this.by = DateTimeUtil.parse(by);
    }

    /**
     * Constructs a new {@code Deadline} task with a pre-parsed {@link DateTimeUtil.TaskDateTime}.
     *
     * @param description the textual description of the deadline task.
     * @param by the deadline date/time object.
     */
    public Deadline(String description, DateTimeUtil.TaskDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the structured date/time of the deadline.
     *
     * @return the deadline date/time object.
     */
    public DateTimeUtil.TaskDateTime getBy() {
        return by;
    }

    /**
     * Checks if this deadline is due on the specified date.
     *
     * @param queryDate date to compare against.
     * @return {@code true} if the deadline date matches the query date.
     */
    @Override
    public boolean isOnDate(LocalDate queryDate) {
        return by.getDate().equals(queryDate);
    }

    /**
     * Returns the formatted string representation of the deadline task,
     * prefixed with the [D] identifier tag and appending the formatted due time.
     *
     * @return formatted string such as "[D][ ] return book (by: Jun 06 2026, 6:00PM)".
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.formatForDisplay() + ")";
    }

    /**
     * Returns the formatted string representation of the deadline task for file storage.
     *
     * @return a pipe-delimited string prefixed with the 'D' tag and containing the storage date/time.
     */
    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by.formatForFile();
    }

    /**
     * Compares this deadline task with another object for equality.
     *
     * @param other the object to compare against.
     * @return {@code true} if both tasks have the same description and due time; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        Deadline deadline = (Deadline) other;
        return by.formatForFile().equals(deadline.by.formatForFile());
    }

    /**
     * Returns a hash code based on the description and due date/time.
     *
     * @return integer hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), by.formatForFile());
    }
}
