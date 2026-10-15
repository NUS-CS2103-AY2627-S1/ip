package summer.task;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a task that should be completed by a specific date.
 */
public class Deadline extends Task {
    private final LocalDate by;

    /**
     * Creates a deadline task with the given description, due date, and completion status.
     *
     * @param description text describing the task
     * @param by date by which the task should be completed
     * @param isDone whether the task is already completed
     */
    public Deadline(String description, LocalDate by, boolean isDone) {
        super(TaskType.DEADLINE, description, isDone);
        this.by = by;
    }

    /**
     * Returns the display text for this deadline task.
     *
     * @return task type, status, description, and due date
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + this.by.format(DATE_DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns this deadline's save line, with the due date appended.
     *
     * @return pipe-delimited save line for this deadline
     */
    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + this.by;
    }

    /**
     * Checks whether the given date is this deadline's due date.
     *
     * @param date date to check
     * @return true if the given date is this deadline's due date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return this.by.equals(date);
    }

    /**
     * Returns this deadline's due date, used to order it when sorting.
     *
     * @return the due date
     */
    @Override
    public Optional<LocalDate> sortKey() {
        return Optional.of(this.by);
    }
}
