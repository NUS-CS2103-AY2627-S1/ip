package bro.task;

import java.time.LocalDate;

import bro.exception.BroException;

/**
 * Represents a task with a deadline.
 * The deadline date and time is parsed and stored as a TaskDateTime object.
 */
public class Deadline extends Task {
    private TaskDateTime deadline;

    /**
     * Constructs a Deadline task with a description and a date/time string.
     *
     * @param description The description of the deadline task.
     * @param deadlineStr The date or date-time string for the deadline.
     * @throws BroException If the date/time string cannot be parsed.
     */
    public Deadline(String description, String deadlineStr) throws BroException {
        super(description);
        this.deadline = TaskDateTime.parse(deadlineStr);
    }

    /**
     * Checks if this deadline task falls on the specified date.
     *
     * @param date The LocalDate to check against.
     * @return True if the deadline date matches the specified date, false
     *         otherwise.
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        return this.deadline.isOnDate(date);
    }

    /**
     * Gets the TaskDateTime deadline object.
     *
     * @return The TaskDateTime instance.
     */
    public TaskDateTime getDeadline() {
        return this.deadline;
    }

    /**
     * Sets the deadline date and time.
     *
     * @param deadline The new TaskDateTime instance.
     */
    public void setDeadline(TaskDateTime deadline) {
        this.deadline = deadline;
    }

    /**
     * Sets the deadline date and time from a date-time string.
     *
     * @param deadlineStr The date or date-time string for the deadline.
     * @throws BroException If the date/time string cannot be parsed.
     */
    public void setDeadline(String deadlineStr) throws BroException {
        this.deadline = TaskDateTime.parse(deadlineStr);
    }

    /**
     * Returns a string representation of the Deadline task for display.
     *
     * @return Formatted string including task type, status icon, description, and
     *         formatted deadline date/time.
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), this.deadline.formatDisplay());
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        Deadline other = (Deadline) obj;
        return java.util.Objects.equals(this.deadline, other.deadline);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), deadline);
    }

    /**
     * Returns a string representation of the Deadline task formatted for file
     * storage.
     *
     * @return Formatted storage string with pipe delimiters.
     */
    @Override
    public String toFileFormat() {
        return formatFilePrefix("D") + " | " + this.deadline.formatFile();
    }
}
