package crow.task;

import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specific date or time.
 */
public class Deadline extends Task {
    private final LocalDateTime deadlineDateTime;

    /**
     * Creates a deadline task.
     *
     * @param description Description of the task.
     * @param deadlineDateTime Date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime deadlineDateTime) {
        super(description);
        this.deadlineDateTime = deadlineDateTime;
    }

    /**
     * Returns the deadline date and time.
     *
     * @return Deadline date and time.
     */
    public LocalDateTime getDeadlineDateTime() {
        return deadlineDateTime;
    }

    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && deadlineDateTime.equals(((Deadline) other).deadlineDateTime);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + deadlineDateTime.format(DISPLAY_DATE_TIME_FORMAT) + ")";
    }
}
