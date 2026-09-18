package plana.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import plana.storage.Storage;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate dueDate;

    /**
     * Creates an incomplete Deadline.
     *
     * @param description the task description.
     * @param dueDate the due date.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        // The deadline parser and storage loader supply a valid date before
        // construction, so a Deadline can always display and serialize it.
        assert dueDate != null : "A deadline must have a due date.";
        this.dueDate = dueDate;
    }

    /**
     * Returns the Deadline in the format used by the storage file.
     *
     * @return the Deadline type, completion status, description, and due date
     */
    @Override
    public String toStorageString() {
        return "D | " + getStorageStatus() + " | " + Storage.escapeField(description)
                + " | " + Storage.escapeField(dueDate.toString());
    }

    /**
     * Returns the Deadline in the format used when displaying the task list.
     *
     * @return the Deadline type, completion status, description, and due date
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDate.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Checks whether this deadline is due on the supplied date.
     *
     * @param date the date to check.
     * @return {@code true} if this deadline is due on the date
     */
    public boolean isOn(LocalDate date) {
        return dueDate.equals(date);
    }

    /**
     * Returns a key made from this deadline's identifying details.
     *
     * @return the task type, description, and due date key.
     */
    @Override
    protected String getDetailsKey() {
        return "D\u0000" + description + "\u0000" + dueDate;
    }
}
