package alexa;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** A task that must be completed by a specified date. */
public class Deadline extends Task {
    /** The input format accepted by Alexa commands and used in saved data. */
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    /** The friendlier date format displayed to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description The task description.
     * @param by The deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns whether another task is an identical deadline.
     *
     * @param other The task to compare with this deadline.
     * @return {@code true} if both deadlines have the same description and date.
     */
    @Override
    public boolean hasSameIdentity(Task other) {
        if (!(other instanceof Deadline deadline)) {
            return false;
        }
        return super.hasSameIdentity(deadline) && by.equals(deadline.by);
    }

    /**
     * Returns this deadline in the line format used for persistence.
     *
     * @return the saved representation of this deadline
     */
    @Override
    public String toStorageString() {
        return "D | " + (isDone() ? "1" : "0") + " | " + getDescription() + " | " + by.format(INPUT_FORMAT);
    }

    /**
     * Returns this deadline in Alexa's user-facing display format.
     *
     * @return the formatted deadline
     */
    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + getDescription() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}