package bambolino.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The required completion date. */
    private final LocalDate dueDate;

    /** The format shown to the user for deadline dates. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /**
     * Creates an incomplete deadline task.
     *
     * @param description The task description.
     * @param dueDate The required completion date.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /**
     * Returns this deadline task in the list-display format.
     *
     * @return The task type, status icon, description, and deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + dueDate.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /** Returns this deadline in a format used by persistent storage. */
    @Override
    public String toStorageString() {
        return "D|" + (isDone ? "1" : "0") + "|" + encode(description)
                + "|" + encode(dueDate.toString());
    }
}
