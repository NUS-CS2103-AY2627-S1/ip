package walnut;

import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specified date and time.
 */
public class Deadline extends Task {
    private LocalDateTime deadline;

    /**
     * Creates a deadline task with the specified description and deadline.
     */
    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        assert deadline != null : "Deadline cannot be null";
        this.deadline = deadline;
    }

    public String getDeadline() {
        return this.deadline.format(Parser.STORAGE_FMT);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + this.deadline.format(Parser.DISPLAY_FMT)
                + ")";
    }
}
