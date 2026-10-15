package bob.task;

import java.time.LocalDateTime;

import bob.exception.BobException;
import bob.util.DatetimeHelper;

/**
 * Represents a task with a deadline.
 */
public class Deadline extends Task {
    private final LocalDateTime deadline;

    /**
     * Constructs a Deadline task with the specified description and due date/time.
     *
     * @param name     the description of the deadline task
     * @param deadline the due date/time
     */
    public Deadline(String name, LocalDateTime deadline) {
        super(name);
        assert deadline != null : "Deadline date-time should not be null";
        this.deadline = deadline;
    }

    /**
     * Returns the due date-time for this deadline.
     *
     * @return the deadline date-time
     */
    public LocalDateTime getDeadline() {
        return this.deadline;
    }

    /**
     * Returns the string representation of the deadline task, including its status,
     * description, and formatted due date/time.
     *
     * @return formatted string representation of this deadline task
     */
    @Override
    public String toString() {
        return String.format(
                "[D]%s (by: %s)", super.toString(), this.deadline.format(DatetimeHelper.OUTPUT_FORMATTER));
    }

    /**
     * Formats the deadline task as a string suitable for persistent storage export.
     *
     * @return delimiter-separated string representation of this deadline task
     */
    @Override
    public String export() {
        return String.format(
                "D%c%s%c%s%c%s",
                STORAGE_DELIMITER, this.getDoneCode(), STORAGE_DELIMITER, this.name,
                STORAGE_DELIMITER, this.deadline.format(DatetimeHelper.ISO_FORMATTER));
    }

    /**
     * Creates an updated copy of this {@link Deadline} task with optional new description and deadline.
     *
     * @param newName     the new description, or null if unchanged
     * @param newDeadline the new deadline date-time, or null if unchanged
     * @param newFrom     the new start date-time (must be null for Deadline)
     * @param newTo       the new end date-time (must be null for Deadline)
     * @return a new updated {@link Deadline} instance with completion status preserved
     * @throws BobException if event date flags (/from, /to) are provided
     */
    @Override
    public Task update(String newName, LocalDateTime newDeadline,
            LocalDateTime newFrom, LocalDateTime newTo) throws BobException {
        if (newFrom != null || newTo != null) {
            throw new BobException("Error: Deadline tasks do not have event dates (/from, /to)");
        }

        String targetName = newName != null ? newName : this.name;
        LocalDateTime targetDeadline = newDeadline != null ? newDeadline : this.deadline;
        Deadline updated = new Deadline(targetName, targetDeadline);
        if (this.isDone) {
            updated.mark();
        }
        return updated;
    }
}
