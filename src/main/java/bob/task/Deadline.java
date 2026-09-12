package bob.task;

import bob.parser.DateTimeParser;

/**
 * Represents a task that must be completed by a particular date or time.
 */
public class Deadline extends Task {
    /** Date and optional time by which the task is due. */
    private final TaskDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description description of the task.
     * @param by date and optional time by which the task is due.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description, TaskType.DEADLINE);
        assert by != null : "A deadline must have a due date";
        this.by = by;
    }

    /**
     * Returns a display representation that includes the due date.
     *
     * @return formatted deadline.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimeParser.formatForDisplay(by) + ")";
    }

    /**
     * Returns the date and optional time by which this task is due.
     *
     * @return deadline date-time.
     */
    public TaskDateTime getBy() {
        return by;
    }
}
