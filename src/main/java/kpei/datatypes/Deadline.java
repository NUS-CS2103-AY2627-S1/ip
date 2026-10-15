package kpei.datatypes;

import java.time.LocalDate;
import java.time.LocalTime;

import kpei.utility.DateTimeParser;

/**
 * Represents a task with a deadline date/time constraint.
 */
public class Deadline extends Task {
    private LocalDate byDate;
    private LocalTime byTime;

    /**
     * Constructs a {@code Deadline} task with specified completion status, description, and due date.
     *
     * @param isMarked Whether this deadline task is marked as completed.
     * @param description The description of the deadline task.
     * @param byDate The date or time string by which the task must be completed.
     */
    public Deadline(boolean isMarked, String description, LocalDate byDate, LocalTime byTime) {
        super(isMarked, description);
        type = "deadline";
        this.byDate = byDate;
        this.byTime = byTime;
    }

    /**
     * Constructs an unmarked {@code Deadline} task with specified description and due date.
     *
     * @param description The description of the deadline task.
     * @param byDate The date or time string by which the task must be completed.
     */
    public Deadline(String description, LocalDate byDate, LocalTime byTime) {
        this(false, description, byDate, byTime);
    }

    @Override
    public String toString() {
        return String.format("%s (by: %s %s)", super.toString(),
                DateTimeParser.formatDate(byDate), DateTimeParser.formatTime(byTime));
    }

    @Override
    public String toFileFormat() {
        return String.format("%s | %s | %s", super.toFileFormat(), byDate.toString(), byTime.toString());
    }

    /**
     * Returns the due date of this deadline.
     *
     * @return The due date.
     */
    public LocalDate getByDate() {
        return byDate;
    }

    /**
     * Returns the due time of this deadline.
     *
     * @return The due time.
     */
    public LocalTime getByTime() {
        return byTime;
    }

    /**
     * Returns a formatted string representation of the due date and time.
     *
     * @return Formatted date string for display.
     */
    public String getFormattedByDate() {
        return String.format("%s %s", DateTimeParser.formatDate(byDate), DateTimeParser.formatTime(byTime));
    }
}

