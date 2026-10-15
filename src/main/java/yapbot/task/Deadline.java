package yapbot.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that has a deadline by which it must be completed.
 */
public class Deadline extends Task {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    protected LocalDate by;

    /**
     * Creates a deadline task with the given description and due date.
     *
     * @param description the task description.
     * @param by the date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "Parser/Storage must supply an already-parsed date";
        this.by = by;
    }

    /**
     * Returns the type icon used to identify a deadline task in the save file.
     *
     * @return "D".
     */
    @Override
    protected String getTypeIcon() {
        return "D";
    }

    /**
     * Returns this deadline task serialized for the save file. The date is
     * written in ISO format (e.g. "2019-10-15"), which {@link LocalDate}
     * parses directly on load without needing a custom pattern.
     *
     * @return the file-format representation of this task.
     */
    @Override
    public String toFileFormat() {
        return appendPriorityField(super.toFileFormat() + " | " + by);
    }

    /**
     * Returns this deadline's due date formatted for display, e.g. {@code "Oct 15 2019"}.
     *
     * @return the human-readable due date.
     */
    public String getFormattedBy() {
        return by.format(DISPLAY_FORMAT);
    }

    /**
     * Returns whether this deadline's due date has already passed, i.e. is
     * strictly before today. A deadline due today is not yet overdue.
     *
     * @return {@code true} if the due date is before today.
     */
    public boolean isOverdue() {
        return by.isBefore(LocalDate.now());
    }

    /**
     * Returns this deadline as it should be displayed to the user, e.g.
     * {@code "[D][ ]return book (by: Oct 15 2019)"}.
     *
     * @return the display representation of this task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + getFormattedBy() + ")";
    }

    /**
     * {@inheritDoc} A deadline additionally must share the same due date.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other) && by.equals(((Deadline) other).by);
    }
}