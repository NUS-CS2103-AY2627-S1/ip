package longfrog.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import longfrog.util.FormatUtils;

/** Represents a task that must be completed by a particular date and time. */
public class Deadline extends Task {
    protected LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param name the task description
     * @param by the due date and time
     */
    public Deadline(String name, LocalDateTime by) {
        super(name);
        this.by = by;
    }

    /**
     * Returns the due date and time.
     *
     * @return the deadline date and time
     */
    public LocalDateTime getBy() {
        return this.by;
    }

    /**
     * Checks whether another task has the same description and deadline.
     *
     * @param other the task to compare with
     * @return whether the tasks have the same duplicate identity
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other) && by.equals(((Deadline) other).by);
    }

    /**
     * Checks whether this deadline is due on a date.
     *
     * @param date the date to check
     * @return whether the deadline is due on the date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /**
     * Returns this deadline in its console display format.
     *
     * @return the formatted deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by.format(FormatUtils.PRINT_FORMAT) + ")";
    }

    /**
     * Returns this deadline in the format used by the save file.
     *
     * @return the serialized deadline
     */
    @Override
    public String toFileFormat() {
        return "D | " + (isDone ? "1" : "0") + " | " + this.name + " | "
                + this.by.format(FormatUtils.INPUT_SAVE_FORMAT);
    }
}
