package duck.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task that needs to be completed by a specific date.
 */
public class Deadline extends Task {
    /** Format used when showing deadline dates to users. */
    private static final DateTimeFormatter DATE_FORMAT_DISPLAY =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Canonical format used for dates stored on disk. */
    private static final DateTimeFormatter DATE_FORMAT_STORAGE =
            DateTimeFormatter.ISO_LOCAL_DATE;

    /** Date by which this task must be completed. */
    private final LocalDate deadlineDate;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description Description of the task.
     * @param by Deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.deadlineDate = Objects.requireNonNull(by, "Deadline date cannot be null.");
        if (this.deadlineDate.getYear() < 1 || this.deadlineDate.getYear() > 9999) {
            throw new IllegalArgumentException("Deadline year must be between 1 and 9999.");
        }
    }

    /**
     * Returns whether another task is a deadline with the same description and date.
     *
     * @param other Task to compare with this deadline.
     * @return true if both deadlines have the same details
     */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!(other instanceof Deadline)) {
            return false;
        }
        Deadline otherDeadline = (Deadline) other;
        return this.description.equals(otherDeadline.description)
                && this.deadlineDate.equals(otherDeadline.deadlineDate);
    }

    /**
     * Returns this deadline with its date in the user-visible display format.
     *
     * @return formatted deadline with its type, status, description, and date
     */
    @Override
    public String toString() {
        return TaskType.DEADLINE.getTag() + super.toString() + " (by: "
                + this.deadlineDate.format(DATE_FORMAT_DISPLAY) + ")";
    }

    /**
     * Formats this deadline with an escaped description and canonical ISO date.
     *
     * @return storage record containing the type, status, description, and date
     */
    @Override
    public String formatForStorage() {
        return TaskType.DEADLINE.getFileCode() + " | " + this.getFileStatus() + " | "
                + this.escapeFileField(this.description) + " | "
                + this.deadlineDate.format(DATE_FORMAT_STORAGE);
    }
}
