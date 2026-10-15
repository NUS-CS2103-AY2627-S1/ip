package clearblue.task;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final TaskDateTime by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description description of the task
     * @param by date or time by which the task should be completed; a
     *     {@code yyyy-MM-dd} value is understood as a real date, anything
     *     else is kept as free text
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = new TaskDateTime(by);
    }

    /**
     * Returns the date or time by which this task should be completed.
     *
     * @return the deadline's {@code by} value
     */
    public TaskDateTime getBy() {
        return by;
    }

    /**
     * Returns this deadline's display text, e.g.
     * {@code "[D][X] return book (by: Jun 06 2019)"}.
     *
     * @return display text for this deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
