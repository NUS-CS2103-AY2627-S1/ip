package happybot.task;

import java.time.LocalDateTime;

/**
 * Represents a task with a due date managed by HappyBot.
 */
public class Deadline extends DatedTask {
    private final LocalDateTime dueDateTime;

    /**
     * Creates a deadline task with the specified description and due date.
     *
     * @param description The deadline description.
     * @param dueDateTime The deadline due date and time.
     * @throws IllegalArgumentException If the due date and time is null.
     */
    public Deadline(String description, LocalDateTime dueDateTime) {
        super(description);
        if (dueDateTime == null) {
            throw new IllegalArgumentException("A deadline must have a due date and time.");
        }

        this.dueDateTime = dueDateTime;
    }

    /**
     * Returns the deadline due date and time.
     *
     * @return The deadline due date and time.
     */
    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    @Override
    public boolean hasSameDetails(Task otherTask) {
        return super.hasSameDetails(otherTask)
                && dueDateTime.equals(((Deadline) otherTask).dueDateTime);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatDate(this.dueDateTime) + ")";
    }
}
