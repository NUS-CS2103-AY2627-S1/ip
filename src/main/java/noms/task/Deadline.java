package noms.task;

import java.time.LocalDate;

import noms.util.DateUtil;

/** Represents a task that must be completed by a specific date. */
public class Deadline extends Task {

    private LocalDate dueDate;

    /**
     * Creates a deadline with the given description and due date.
     *
     * @param description the text describing the task
     * @param dueDate the date the task is due
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /** Returns true if this deadline falls on the given date. */
    public boolean occursOn(LocalDate date) {
        return dueDate.equals(date);
    }

    /** Returns this deadline's due date. */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Replaces this deadline's due date without changing its other details.
     *
     * @param newDueDate the replacement due date
     */
    public void rescheduleTo(LocalDate newDueDate) {
        dueDate = newDueDate;
    }

    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && dueDate.equals(((Deadline) other).dueDate);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateUtil.format(dueDate) + ")";
    }

    @Override
    public String toFileFormat() {
        // LocalDate.toString() writes ISO yyyy-MM-dd, which round-trips
        // exactly with LocalDate.parse when the save file is read back.
        return "D | " + super.toFileFormat() + " | " + escape(dueDate.toString());
    }
}
