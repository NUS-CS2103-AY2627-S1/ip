package bobby.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import bobby.storage.Storage;

/**
 * A task that must be completed by a specified date and time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH);

    private final LocalDateTime deadlineDateTime;

    /**
     * Creates a deadline task.
     *
     * @param description the task description
     * @param deadlineDateTime the deadline date and time
     */
    public Deadline(String description, LocalDateTime deadlineDateTime) {
        super(description);
        assert deadlineDateTime != null : "Deadline date and time must not be null";
        this.deadlineDateTime = deadlineDateTime;
    }

    /**
     * Returns whether this deadline has the same description and appointed time as another task.
     *
     * @param other the task to compare with.
     * @return {@code true} when both tasks represent the same deadline.
     */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && deadlineDateTime.equals(((Deadline) other).deadlineDateTime);
    }

    /**
     * Returns this deadline in the on-disk format used by {@link Storage}.
     *
     * @return a line that represents this deadline in the save file
     */
    @Override
    public String toFileString() {
        return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + deadlineDateTime
                + " | " + getCompletionDateTimeFileValue();
    }

    /**
     * Returns this deadline task in the standard list format.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + description + " (appointed for: "
                + deadlineDateTime.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
