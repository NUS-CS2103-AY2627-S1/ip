package kiaw.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private LocalDate by;

    /**
     * Creates a deadline with the specified description and due date.
     *
     * @param description description of the deadline
     * @param by date by which the task should be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the symbol representing a deadline task.
     *
     * @return "D"
     */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns the deadline details in a user-friendly format.
     *
     * @return formatted deadline details
     */
    @Override
    public String getDetails() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM dd yyyy");

        return description
                + " (by: "
                + by.format(formatter)
                + ")";
    }

    /**
     * Returns the due date of this deadline.
     *
     * @return deadline due date
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns the deadline date used for chronological sorting.
     *
     * @return deadline due date
     */
    @Override
    public LocalDate getSortDate() {
        return by;
    }
}
