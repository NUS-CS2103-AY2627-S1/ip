package notmarth.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import notmarth.parser.DateTimeParser;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;
    private final boolean hasTime;

    /**
     * Creates an unfinished deadline task from a supported date or time text.
     *
     * @param description the task description.
     * @param by the date or time by which the task should be completed.
     * @throws DateTimeParseException if {@code by} is not a supported date
     *                                or date-time format.
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        DateTimeParser.ParsedDateTime parsedDateTime = DateTimeParser.parse(by);
        this.by = parsedDateTime.value();
        this.hasTime = parsedDateTime.hasTime();
    }

    /**
     * Creates a deadline from an already parsed date and time.
     *
     * @param description the task description.
     * @param by the deadline date and time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        if (by == null) {
            throw new IllegalArgumentException("A deadline needs a date or time.");
        }
        this.by = by;
        this.hasTime = true;
    }

    /**
     * Creates a date-only deadline from an already parsed date.
     *
     * @param description the task description.
     * @param by the deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        if (by == null) {
            throw new IllegalArgumentException("A deadline needs a date.");
        }
        this.by = by.atStartOfDay();
        this.hasTime = false;
    }

    /**
     * Returns the typed deadline value.
     *
     * @return the deadline date and time, at midnight for a date-only deadline.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns the unambiguous value used in the task archive.
     *
     * @return an ISO date or ISO local date-time.
     */
    public String getByForStorage() {
        return DateTimeParser.formatForStorage(by, hasTime);
    }

    /** Checks whether another task has the same description and deadline. */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return other instanceof Deadline deadline
                && super.hasSameDetailsAs(other)
                && getByForStorage().equals(deadline.getByForStorage());
    }

    /**
     * Checks whether this deadline falls on a calendar date.
     *
     * @param date the date to check.
     * @return {@code true} when the deadline is due on the date.
     */
    public boolean isDueOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /**
     * Returns this task with its formatted deadline and type marker.
     *
     * @return the formatted deadline task.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimeParser.format(by, hasTime) + ")";
    }
}
