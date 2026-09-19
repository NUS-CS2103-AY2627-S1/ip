package carl.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import carl.util.DateParser;

/**
 * Represents a task with a specific deadline.
 */
public class Deadline extends Task {

    /** Date and time by which the task is due. */
    protected final LocalDateTime by;

    /**
     * Constructs a new Deadline task with the specified item and deadline.
     * The task status is initialized to NOT_DONE by default.
     *
     * @param name The description or name of the task.
     * @param by   The date and time the task is due.
     */
    public Deadline(String name, LocalDateTime by) {
        super(name, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Constructs a Deadline task with a specified completion status.
     * This is typically used when loading tasks from a saved file.
     *
     * @param name   The description or name of the task.
     * @param status The completion status of the task.
     * @param by     The date and time the task is due.
     */
    public Deadline(String name, TaskStatus status, LocalDateTime by) {
        super(name, status, TaskType.DEADLINE);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DateParser.getDisplayFormatter()) + ")";
    }

    @Override
    public String toSaveFormat() {
        return String.format("%s | %s", super.toSaveFormat(), by.format(DateParser.getStorageFormatter()));
    }

    @Override
    protected String getIdentity() {
        return super.getIdentity() + "|" + by;
    }

    @Override
    protected boolean isOnDate(LocalDate targetDate) {
        return targetDate.isEqual(this.by.toLocalDate());
    }
}
