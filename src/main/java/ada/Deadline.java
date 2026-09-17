package ada;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a deadline‑type task that stores a description and a due‑date time.
 * Subclass of {@code Task}. Displays and persists the "by" deadline information.
 */
public class Deadline extends Task {

    private LocalDateTime by;

    /**
     * Constructs a Deadline task with given description and due datetime.
     *
     * @param description text content of the deadline task
     * @param by the due date‑time for this task
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "A deadline must have a due date and time";
        this.by = by;
    }

    /**
     * Returns the deadline date and time.
     *
     * @return deadline date and time
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns human‑readable string for console display.
     * Prefixes "[D]" and appends formatted due‑date information.
     *
     * @return formatted string shown to users
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(this.by.toLocalDate()
                .format(DateTimeFormats.DISPLAY_DATE));
        if (!this.by.toLocalTime()
                .equals(DateTimeFormats.DEFAULT_TIME)) {
            output.append(" ")
                    .append(this.by.format(DateTimeFormats.DISPLAY_TIME));
        }

        return "[D]" + super.toString() + " (by: " + output + ")";
    }

    /**
     * Generates a storage‑compatible text line for saving into file.
     * Uses "D | " prefix and ISO‑8601 format for the deadline datetime.
     *
     * @return formatted line for persistent storage
     */
    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + this.by.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
