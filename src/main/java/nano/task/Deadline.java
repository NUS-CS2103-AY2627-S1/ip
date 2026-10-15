package nano.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import nano.DateTimeUtil;
import nano.NanoException;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private LocalDateTime by;

    /**
     * Creates a deadline task with the specified description and deadline.
     *
     * @param description description of the deadline task.
     * @param by date or time by which the task should be completed.
     * @throws NanoException if the description or deadline is empty.
     */
    public Deadline(String description, String by) throws NanoException {
        super(description);

        if (by.trim().isEmpty()) {
            throw new NanoException("The /by date of a deadline cannot be empty.");
        }

        try {
            this.by = LocalDateTime.parse(
                    by.trim(),
                    DateTimeUtil.STORAGE_FORMATTER
            );
        } catch (DateTimeParseException e) {
            throw new NanoException("The deadline date must be in yyyy-mm-dd HHmm format.");
        }
    }

    @Override
    public String toStorageString() {
        return "D | " + (isDone() ? "1" : "0")
                + " | " + getPriorityText()
                + " | " + getDescription()
                + " | " + DateTimeUtil.formatForStorage(by);
    }


    @Override
    public String toString() {
        return "[D]" + getStatusIcon()
                + " " + getDescription()
                + " (by: " + DateTimeUtil.formatForDisplay(by) + ")"
                + " (priority: " + getPriorityText() + ")";
    }
}
