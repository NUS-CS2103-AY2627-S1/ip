package jiji.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jiji.parser.DateTimeUtil;

/**
 * Represents a task with a deadline date/time constraint.
 */
public class Deadline extends Task {

    /** Deadline time or date by which the task must be completed. */
    private final String by;

    /**
     * Constructs a new Deadline task with the specified description and deadline time.
     *
     * @param description Description of the deadline task.
     * @param by Deadline date or time string.
     */
    public Deadline(String description, String by) {
        super(description);
        assert by != null && !by.isBlank() : "Deadline 'by' date/time cannot be null or blank";
        this.by = by;
    }

    /**
     * Checks if this deadline is overdue relative to a given date.
     * Completed deadlines are not considered overdue.
     *
     * @param today The reference date.
     * @return True if incomplete and the deadline is strictly before the reference date, false otherwise.
     */
    public boolean isOverdue(LocalDate today) {
        if (isDone() || today == null) {
            return false;
        }
        LocalDate date = getDeadlineDate();
        return date != null && date.isBefore(today);
    }

    /**
     * Returns the parsed LocalDate of this deadline, or null if not a recognized date.
     *
     * @return The deadline LocalDate, or null if not applicable.
     */
    public LocalDate getDeadlineDate() {
        LocalDate parsedDate = DateTimeUtil.parseLocalDate(by);
        if (parsedDate != null) {
            return parsedDate;
        }
        LocalDateTime parsedDateTime = DateTimeUtil.parseLocalDateTime(by);
        if (parsedDateTime != null) {
            return parsedDateTime.toLocalDate();
        }
        return null;
    }

    /**
     * Returns the storage text representation for a Deadline task with standardized date formatting.
     *
     * @return Formatted string for file storage: {@code "D | <isDone> | <description> | <by>"}.
     */
    @Override
    public String toFileFormat() {
        return TaskType.DEADLINE.getCode() + " | " + super.toFileFormat() + " | "
                + DateTimeUtil.formatForStorage(by);
    }

    /**
     * Returns the string representation of the deadline task, prefixed with [D] and suffixing formatted (by: ...).
     *
     * @return Formatted deadline string representation.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeUtil.formatForDisplay(by) + ")";
    }

    /**
     * Checks if this deadline is a duplicate of another task (same description and deadline constraint).
     *
     * @param other The other task to compare against.
     * @return True if both tasks are deadlines with matching description and by parameters.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        if (!super.isDuplicateOf(other)) {
            return false;
        }
        Deadline otherDeadline = (Deadline) other;
        return this.by.trim().equalsIgnoreCase(otherDeadline.by.trim());
    }
}
