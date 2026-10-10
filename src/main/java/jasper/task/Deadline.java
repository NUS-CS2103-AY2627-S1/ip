package jasper.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that needs to be completed by a specific deadline.
 */
public class Deadline extends Task {
    /** Deadline of the task */
    private final LocalDateTime by;

    /**
     * Constructs a Deadline task with the specified description and deadline.
     *
     * @param description Description of the task.
     * @param by Deadline date and time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    public String getBy() {
        return by.toString();
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");
        return String.format("[D]%s (by: %s)", super.toString(), by.format(fmt));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Deadline other) {
            return super.equals(other) && this.by.equals(other.by);
        }
        return false;
    }
}
