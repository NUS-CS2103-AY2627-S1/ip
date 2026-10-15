package zen.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a task that is due at a specified date and time. */
public class Deadline extends Task {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss");
    protected LocalDateTime dueBy;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the deadline description.
     * @param dueBy the due date and time.
     */
    public Deadline(String description, LocalDateTime dueBy) {
        super(description);
        this.dueBy = dueBy;
    }

    /**
     * Creates an incomplete deadline task with a priority.
     *
     * @param description the deadline description
     * @param dueBy the due date and time
     * @param priority the deadline priority
     */
    public Deadline(String description, LocalDateTime dueBy, Priority priority) {
        super(description, priority);
        this.dueBy = dueBy;
    }

    /**
     * Returns this deadline in the file format used for persistence.
     *
     * @return a pipe-delimited deadline record
     */
    @Override
    public String toStorageString() {
        return String.format("D | %d | %s | %s | %s", this.isDone ? 1 : 0, this.description,
                this.dueBy, priority.getDisplayValue());
    }

    /**
     * Returns true if this deadline's due date falls on the given date,
     * regardless of the time component.
     *
     * @param date the date to compare against this deadline's due date
     * @return true if this deadline is due on the given date, false otherwise
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return dueBy.toLocalDate().equals(date);
    }

    @Override
    public String toString() {
        return formatWithPriority(String.format("[D][%s] %s (by: %s)", getStatusIcon(), description,
                this.dueBy.format(FORMATTER)));
    }
}
