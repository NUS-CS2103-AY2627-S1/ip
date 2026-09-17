package lobby.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a given date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description the task description.
     * @param by the deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Formats this deadline as one line in the save file.
     *
     * @return the serialized deadline
     */
    @Override
    public String toDataString() {
        return "D | " + toDataFields() + " | " + by;
    }

    /**
     * Formats this deadline for display in the task list.
     *
     * @return the task type, completion status, description, and deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
