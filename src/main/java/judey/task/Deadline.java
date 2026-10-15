package judey.task;

import judey.exception.JudeyException;

import java.time.LocalDateTime;

/**
 * Represents a task with a deadline date and time
 */
public class Deadline extends Task {
    protected LocalDateTime by;

    /**
     * Constructs a {@code Deadline} task with a description and target date/time string
     * @param desc The description of the deadline task
     * @param by The date and time by which the task should be completed by, parsed into a {@code LocalDateTime}
     * @throws JudeyException If the given date/time string cannot be parsed using supported formats
     */
    public Deadline(String desc, String by) throws JudeyException {
        super(desc);
        this.by = DateTimeUtil.parse(by);
        assert this.by != null
                : "parseDateTime() either returns a valid LocalDateTime or throws JudeyException; it should never "
                + "leave 'by' as null once this constructor completes successfully";
    }

    /**
     * Getter for the date and time by which the task must be completed by
     * @return {@code LocalDateTime} due date
     */
    public LocalDateTime getBy() {
        return this.by;
    }

    @Override
    public String toFileString() {
        return TYPE_DEADLINE + " | " + (isDone ? DONE_MARKER : NOT_DONE_MARKER) + " | " + description + " | " + by;
    }

    @Override
    public String toString() {
        return "[" + Task.TYPE_DEADLINE + "]" + super.toString() + " (by: " + DateTimeUtil.format(by) + ")\n";

    }
}
