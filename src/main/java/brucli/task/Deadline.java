package brucli.task;

import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a due time.
 */
public class Deadline extends Task {
    private final LocalDateTime dueDate;

    /**
     * Creates a deadline with its internal ID, description, and due time.
     */
    public Deadline(int id, String description, LocalDateTime dueDate) {
        super(id, description);
        this.dueDate = dueDate;
    }

    @Override
    public String serialize() {
        return String.format(
                "D | %d | %s | %s",
                isDone ? 1 : 0,
                description,
                DateTimes.serialize(dueDate)
        );
    }

    @Override
    protected String getType() {
        return "D";
    }

    @Override
    protected LocalDateTime getDateTime() {
        return dueDate;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimes.display(dueDate) + ")";
    }
}
