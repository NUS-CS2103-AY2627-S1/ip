package edith.task;

import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specified time or date.
 */
public class Deadline extends Task {
    /** The deadline text supplied by the user. */
    protected String by;

    /** Parsed deadline, when the input uses a supported date format. */
    protected LocalDateTime byDateTime;

    /**
     * Creates a deadline task.
     *
     * @param description the task description
     * @param by the deadline text
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
        this.byDateTime = DateTimeParser.parse(by);
    }

    @Override
    public LocalDateTime getDateTimeForSorting() {
        return byDateTime == null ? DateTimeParser.parseForSorting(by) : byDateTime;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && normalize(by).equals(normalize(((Deadline) other).by));
    }

    @Override
    protected String getTypeCode() {
        return "D";
    }

    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + (byDateTime == null ? by : byDateTime);
    }

    /**
     * Returns this task with its type, status, and deadline.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        String displayDate = byDateTime == null ? by : DateTimeParser.format(byDateTime);
        return "[D]" + super.toString() + " (by: " + displayDate + ")";
    }
}
