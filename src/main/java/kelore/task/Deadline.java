package kelore.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import kelore.storage.Storage;

/** Represents a task that must be completed by a specified date or time. */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter
            .ofPattern("MMM d uuuu, h:mm a", Locale.ENGLISH);
    /** Date and time by which this task must be completed. */
    protected LocalDateTime by;

    /**
     * Creates a deadline with its description and due date and time.
     *
     * @param description Description of the task.
     * @param by Date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "A deadline must have a due date and time";
        this.by = by;
    }

    /** Returns a displayable description of this deadline and its completion status. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /** Returns this deadline in the format used by the data file. */
    @Override
    public String toStorageString() {
        return Storage.joinFields("D", getStorageStatus(), description, by.toString());
    }

    /**
     * Returns whether this deadline is due on the specified calendar date.
     *
     * @param date Calendar date to check.
     * @return True if the deadline is due on the date; false otherwise.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /** Returns whether another deadline has the same description and due time. */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && by.equals(((Deadline) other).by);
    }

    /** Returns an independent copy of this deadline. */
    @Override
    Deadline copy() {
        Deadline copiedDeadline = new Deadline(description, by);
        if (isDone) {
            copiedDeadline.markAsDone();
        }
        return copiedDeadline;
    }
}
