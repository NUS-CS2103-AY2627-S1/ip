package ari.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a deadline.
 */
public class DeadlineTask extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate deadlineDate;
    private final String deadlineText;

    /**
     * Creates a deadline task from separately parsed fields.
     *
     * @param description Deadline task description.
     * @param deadlineText Free-form deadline of the task.
     */
    public DeadlineTask(String description, String deadlineText) {
        super(description, "D");
        this.deadlineDate = null;
        this.deadlineText = deadlineText;
    }

    /**
     * Creates a deadline task with a parsed date.
     *
     * @param description Deadline task description.
     * @param deadlineDate Date by which the task must be completed.
     */
    public DeadlineTask(String description, LocalDate deadlineDate) {
        super(description, "D");
        this.deadlineDate = deadlineDate;
        this.deadlineText = null;
    }

    @Override
    public DeadlineTask copy() {
        DeadlineTask copy = this.deadlineDate == null
                ? new DeadlineTask(this.description, this.deadlineText)
                : new DeadlineTask(this.description, this.deadlineDate);
        copy.isDone = this.isDone;
        return copy;
    }

    /**
     * Returns the deadline associated with this task.
     *
     * @return Deadline text.
     */
    public String getDeadline() {
        assert (this.deadlineDate != null) != (this.deadlineText != null)
                : "A deadline must have exactly one representation";
        if (this.deadlineDate != null) {
            return this.deadlineDate.format(DISPLAY_DATE_FORMATTER);
        }
        return this.deadlineText;
    }

    /**
     * Returns this deadline task as a storage record.
     *
     * @return Storage representation of this deadline task.
     */
    @Override
    public String toDataString() {
        String status = this.isDone
                ? "1"
                : "0";

        return String.format(
                "%s | %s | %s | %s",
                this.type,
                status,
                this.description,
                this.getStorageDeadline()
        );
    }

    /**
     * Returns this deadline task in a user-readable format.
     *
     * @return User-readable representation of this deadline task.
     */
    @Override
    public String toString() {
        String box;

        if (this.isDone) {
            box = "[X]";
        } else {
            box = "[ ]";
        }

        return String.format(
                "[%s]%s %s (by: %s)",
                this.type, box, this.description, this.getDeadline());
    }

    /**
     * Returns the deadline in the stable format used by storage.
     *
     * @return ISO date or unchanged free-form deadline text.
     */
    private String getStorageDeadline() {
        assert (this.deadlineDate != null) != (this.deadlineText != null)
                : "A deadline must have exactly one representation";
        if (this.deadlineDate != null) {
            return this.deadlineDate.toString();
        }
        return this.deadlineText;
    }
}
