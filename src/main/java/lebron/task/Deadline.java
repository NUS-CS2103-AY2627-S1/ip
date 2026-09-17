package lebron.task;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a Deadline, a Task that must be completed by a specific date/time.
 */
public class Deadline extends Task {
    private LocalDateTime deadline;

    /**
     * Constructs a Deadline with the given description and due date/time.
     *
     * @param description Task description.
     * @param deadline The date/time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        assert deadline != null : "Deadline's due date/time should not be null";
        this.deadline = deadline;
    }

    @Override
    public String reformat() {
        return "D | " + super.reformat() + " | " + this.deadline;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.deadline.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        Deadline other = (Deadline) obj;
        return deadline.equals(other.deadline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deadline);
    }
}
