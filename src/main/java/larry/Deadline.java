package larry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private LocalDate by;

    /**
     * Creates a deadline task with the specified description and due date.
     *
     * @param description Description of the deadline task.
     * @param by Due date in ISO date format.
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = LocalDate.parse(by);
    }

    /**
     * Returns the due date in ISO date format.
     *
     * @return Due date in yyyy-mm-dd format.
     */
    public String getBy() {
        return by.toString();
    }

    /**
     * Returns a display-friendly representation of this deadline.
     *
     * @return Deadline type, status, description, and due date.
     */
    @Override
    public String toString() {
        return super.toString()
                + " (by: "
                + by.format(DateTimeFormatter.ofPattern("MMM d yyyy", Locale.US))
                + ")";
    }
}
