package botavius.tasklist;

import java.time.LocalDateTime;

/** A task that can only be done after a specified date and time. */
public class DoAfter extends Task {

    /** Earliest date and time at which the task can be done. */
    private LocalDateTime after;

    /**
     * Creates an incomplete do-after task.
     *
     * @param description task description
     * @param after earliest date and time for doing the task
     */
    public DoAfter(String description, String after) {
        super(description);
        this.after = TaskDateTime.parse(after, "do-after time");
    }

    /** Returns the earliest date and time at which the task can be done.
     *
     * @return the do-after date and time
     */
    public LocalDateTime getAfter() {
        return after;
    }

    /** Formats the do-after task for display.
     *
     * @return task text with its earliest allowable time
     */
    @Override
    public String toString() {
        return "[A]" + super.toString() + " (after: "
                + TaskDateTime.toDisplayString(after) + ")";
    }

    /** Returns the task in the format used when persisting it.
     *
     * @return the do-after task's storage representation
     */
    @Override
    public String toStorageString() {
        return "[A]" + super.toStorageString() + " after: "
                + TaskDateTime.toStorageString(after);
    }
}
