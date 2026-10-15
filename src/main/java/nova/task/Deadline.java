package nova.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Represents a task that must be completed by a calendar date. */
public class Deadline extends Task {
    /** The format used when showing dates to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    /** The deadline date. */
    protected LocalDate by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the task
     * @param by the deadline date
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /** Returns the task description with its deadline suffix. */
    @Override
    public String getDisplayDescription() {
        return description + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /** Returns whether another task has the same type, description, and deadline date. */
    @Override
    public boolean hasSameIdentity(Task other) {
        return super.hasSameIdentity(other) && other instanceof Deadline deadline && by.equals(deadline.by);
    }

    /**
     * Returns this deadline in the format used for persistent storage.
     *
     * @return the deadline type, completion state, description, and deadline separated by pipes
     */
    @Override
    public String toStorageString() {
        return getBaseStorageString() + " | " + by + getTagsStorageSuffix();
    }

    /**
     * Returns this deadline task with its type prefix and deadline.
     *
     * @return the deadline display string
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
