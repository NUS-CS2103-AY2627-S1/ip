package xuan.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a deadline task with a specific due date.
 */
public class Deadline extends Task {
    private LocalDate by;

    /**
     * Creates a Deadline task with the given description and due date.
     *
     * @param description the description of the deadline task
     * @param by the due date of the deadline
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the due date of the deadline.
     *
     * @return the due date of the deadline
     */
    public LocalDate getBy() {
        return by;
    }

    @Override
    public String toString() {
        String formattedDate = by.format(
                DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        return "[D]" + super.toString() + " (by: " + formattedDate + ")";
    }
}
