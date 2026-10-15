package zeus.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that must be completed by a specified date. */
public class Deadline extends Task {
    /** Format used to present stored dates to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline task.
     * @param description description of the task
     * @param dueDate date by which the task must be completed
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /**
     * Returns this deadline in the format stored on disk.
     * @return serialized deadline task
     */
    @Override
    public String toDataString() {
        return "D | " + super.toDataString() + " | " + dueDate;
    }

    /**
     * Returns this task with its deadline type icon and deadline text.
     * @return formatted deadline task
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + dueDate.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
