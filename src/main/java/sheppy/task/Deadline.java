package sheppy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import sheppy.SheppyException;

/** Represents a task that must be completed by a specified date or time. */
public class Deadline extends Task {
    /** The date by which the task should be completed. */
    private final LocalDate by;

    /** The format used when displaying a deadline to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * Creates a deadline task.
     *
     * @param description the text describing the task
     * @param by the date by which it should be completed
     * @throws SheppyException if the description is empty or the date is null
     */
    public Deadline(String description, LocalDate by) throws SheppyException {
        super(description);
        if (by == null) {
            throw new SheppyException("a deadline needs a date after /by.");
        }
        this.by = by;
    }

    /** @return the deadline task type */
    @Override
    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    /** @return the description followed by the formatted deadline */
    @Override
    public String getDisplayDescription() {
        return super.getDisplayDescription() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /** @return the task serialized with its ISO deadline date */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + by;
    }
}
