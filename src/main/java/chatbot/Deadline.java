package chatbot;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * A Deadline is a Task with a specified deadline.
 */
public class Deadline extends Task {
    private final LocalDateTime deadline;

    /**
     * Creates a Deadline object with the given deadline and task description.
     *
     * @param description Task description.
     * @param deadlineString Deadline in {@code yyyy-MM-dd HHmm} format.
     */
    public Deadline(String description, String deadlineString) {
        super(description);
        if (deadlineString == null || deadlineString.isBlank()) {
            throw new InvalidInputException("Hey bro you gave left the datetime blank. Please"
                + " input the datetime in this format:\n" + DateTimeFormats.INPUT_FORMAT);
        }
        try {
            this.deadline = LocalDateTime.parse(deadlineString, DateTimeFormats.STORAGE);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Hey bro you gave the deadline in an invalid datetime format. Please"
                + " input the datetime in this format:\n" + DateTimeFormats.INPUT_FORMAT);
        }
        // The exception above ensures a Deadline never exists without a parsed date.
        assert this.deadline != null : "A deadline must have a parsed date";
    }

    /**
     * Returns the date and time by which this task should be completed.
     *
     * @return This task's deadline.
     */
    public LocalDateTime getDeadline() {
        return this.deadline;
    }

    /**
     * Checks if a given deadline is a duplicate of the current object.
     *
     * @param d Input deadline to compare against current instance.
     * @return True if input deadline has the same deadline and description as current object, False otherwise.
     */
    public boolean isDuplicate(Deadline d) {
        return super.hasSameDescription(d.getDescription())
            && this.deadline.equals(d.getDeadline());
    }

    /**
     * Returns the deadline's type marker, status, description, and due date.
     *
     * @return Formatted deadline description.
     */
    @Override
    public String toString() {
        return "[D] " + this.getCompletionStatus() + " " + this.getDescription()
            + " (by: " + deadline.format(DateTimeFormats.DISPLAY_DATE_TIME) + ")";
    }

}
