package wodan.task;

import java.time.LocalDate;

/**
 * A task that must be done by a given deadline.
 */
public class Deadline extends Task {
    /** When this task is due. */
    private TaskDateTime dueAt;

    /**
     * Creates a deadline with the given due date or date-time.
     *
     * @param description What must be done.
     * @param dueAt When it is due.
     */
    public Deadline(String description, TaskDateTime dueAt) {
        super(description);
        this.dueAt = dueAt;
    }

    /**
     * Returns this deadline as {@code [D][status] description (by: when)}.
     *
     * @return Display text for list replies.
     */
    @Override
    public String toString() {
        return String.format("[D][%s] %s (by: %s)",
                getStatusIcon(), getDescription(), dueAt.toDisplayString());
    }

    /**
     * Returns when this deadline is due.
     *
     * @return The due date or date-time.
     */
    public TaskDateTime getDueAt() {
        return this.dueAt;
    }

    /**
     * Returns whether {@code other} is a deadline with the same description and due time.
     *
     * @param other Task to compare, which may be {@code null}.
     * @return {@code true} if both deadlines would look like the same quest.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }
        return dueAt.hasSameValue(((Deadline) other).dueAt);
    }

    /**
     * Returns {@code true} if the due date is {@code date}.
     *
     * @param date Date to check.
     * @return Whether this deadline falls on {@code date}.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return dueAt.toLocalDate().equals(date);
    }

    /**
     * Returns this deadline as {@code D | 0|1 | description | when | category}.
     *
     * @return One save-file line.
     */
    @Override
    public String toStorageString() {
        return toStoragePrefix(STORAGE_TYPE_DEADLINE) + " | " + dueAt.toStorageString()
                + " | " + getCategory();
    }
}
