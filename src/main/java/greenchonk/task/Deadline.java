package greenchonk.task;

import java.time.LocalDate;

/**
 * Represents a task that must be completed by a particular date.
 */
public class Deadline extends Task {
    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline with the given description and due value.
     *
     * @param description the deadline description.
     * @param dueDate the date by which the task should be completed.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /**
     * Returns the date by which this task should be completed.
     *
     * @return the deadline's due date.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && dueDate.equals(((Deadline) other).dueDate);
    }

    @Override
    public Deadline withDescription(String description) {
        Deadline editedDeadline = new Deadline(description, dueDate);
        copyStatusTo(editedDeadline);
        return editedDeadline;
    }

    /**
     * Returns a copy of this deadline with a replacement due date.
     * The copy retains this deadline's description and completion status.
     *
     * @param dueDate the replacement due date.
     * @return the updated deadline copy.
     */
    public Deadline withDueDate(LocalDate dueDate) {
        Deadline editedDeadline = new Deadline(getDescription(), dueDate);
        copyStatusTo(editedDeadline);
        return editedDeadline;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return dueDate.equals(date);
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns the task display text with its formatted due date.
     *
     * @return the deadline's display text
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + TaskDate.format(dueDate) + ")";
    }
}
