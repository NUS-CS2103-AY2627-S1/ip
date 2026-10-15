package kojispawn.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private final LocalDate dateBy;

    /**
     * Creates an incomplete deadline.
     *
     * @param description Description of the deadline task.
     * @param dateBy Date by which the task should be completed.
     */
    public Deadline(String description, LocalDate dateBy) {
        super(description);
        this.dateBy = dateBy;
    }

    @Override
    Task copy() {
        Deadline copy = new Deadline(description, dateBy);
        copyCompletionStatusTo(copy);
        return copy;
    }

    @Override
    public String toDataString() {
        return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + dateBy;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return dateBy.equals(date);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dateBy.format(DISPLAY_FORMAT) + ")";
    }
}
