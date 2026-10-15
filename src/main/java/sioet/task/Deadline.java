package sioet.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates a deadline task with its description and due time.
     *
     * @param description the task description
     * @param by          the due date and time
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "Deadline time should not be null";
        this.by = by;
    }

    @Override
    protected String getTaskType() {
        return "D";
    }

    @Override
    protected String getDetails() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

        return " (by: " + by.format(formatter) + ")";
    }

    /**
     * Returns the deadline's due date and time.
     *
     * @return the due date and time
     */
    public LocalDateTime getBy() {
        return by;
    }
}
