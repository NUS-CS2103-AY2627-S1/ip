package kdb;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a task that must be completed by a date and time. */
public class Deadline extends Task {
    private LocalDateTime by;

    /**
     * Creates a deadline with a description and due date/time.
     *
     * @param description text describing the deadline
     * @param by date and time by which the task should be completed
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /** Creates a deadline with a priority. */
    public Deadline(String description, LocalDateTime by, Priority priority) {
        super(description, priority);
        this.by = by;
    }

    /** Returns a human-readable representation of this deadline. */
    @Override
    public String toString() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

        return "[D]" + super.toString()
                + " (by: " + by.format(formatter) + ")";
    }

    /** Returns the representation used when saving this deadline. */
    @Override
    public String toFileFormat() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("d/M/uuuu HHmm");

        return "D | " + (isDone() ? "1" : "0")
                + " | " + getDescription()
                + " | " + by.format(formatter);
    }
}
