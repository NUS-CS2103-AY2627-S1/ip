package sal;

/**
 * A task that must be done by a specific date/time.
 */
public class Deadline extends Task {
    protected TaskDateTime by;

    /**
     * Creates a deadline with the given description and due date/time.
     *
     * @param description Text describing the deadline.
     * @param by Due date or date-time.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return withTags("[D]" + super.toString() + " (by: " + DateTimeParser.formatForDisplay(by) + ")");
    }
}
