package caesar.task;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private String by;

    /** Creates a deadline task with its description and deadline text. */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline text used when saving the task.
     */
    public String getBy() {
        return by;
    }

    /** Updates this deadline to a new validated display-date value. */
    public void reschedule(String newBy) {
        by = newBy;
    }

    /** Returns the deadline in the task-list display format. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
