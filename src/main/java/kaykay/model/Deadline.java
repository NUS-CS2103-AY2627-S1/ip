package kaykay.model;

import java.time.LocalDateTime;

import kaykay.parser.DateTimeParser;
import kaykay.storage.Storage;

/**
 * A task that must be completed by a specified date or time.
 *
 */
public class Deadline extends Task {
    /** The date and time by which this task should be completed. */
    private final LocalDateTime deadlineDateTime;

    /**
     * Creates a deadline with a typed date/time value.
     *
     * @param description what needs to be done.
     * @param deadlineDateTime the deadline date and time.
     */
    public Deadline(String description, LocalDateTime deadlineDateTime) {
        super(description);
        this.deadlineDateTime = deadlineDateTime;
    }

    @Override
    public boolean hasSameDetails(Task otherTask) {
        return otherTask instanceof Deadline otherDeadline
                && super.hasSameDetails(otherDeadline)
                && deadlineDateTime.equals(otherDeadline.deadlineDateTime);
    }

    /**
     * Returns the deadline's user-facing display text with its due date/time.
     *
     * @return the formatted deadline text.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.formatForDisplay(deadlineDateTime) + ")";
    }

    /**
     * Returns the deadline in the format used by the task storage file.
     *
     * @return the serialized deadline.
     */
    @Override
    public String toFileFormat() {
        return String.format("D | %d | %s | %s", isDone() ? 1 : 0,
                Storage.escape(getDescription()), Storage.escape(DateTimeParser.format(deadlineDateTime)));
    }
}
