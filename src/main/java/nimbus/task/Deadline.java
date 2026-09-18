package nimbus.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Represents a task that must be completed by a deadline. */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private final LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description Description of the task.
     * @param by Deadline date in ISO format.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDate.parse(by);
    }

    /** Returns the deadline in the storage input format. */
    public String getStorageDate() {
        return by.toString();
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
