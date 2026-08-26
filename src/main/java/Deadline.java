/**
 * Represents a task that must be completed by a specified date, optionally with a time.
 */
public class Deadline extends Task {
    private final TaskDateTime deadline;

    /**
     * Creates an incomplete deadline with the given description and due date.
     *
     * @param description the deadline description.
     * @param deadline the date (and optional time) by which the task must be completed.
     */
    public Deadline(String description, TaskDateTime deadline) {
        super(description);
        this.deadline = deadline;
    }

    @Override
    protected String getTypeCode() {
        return "D";
    }

    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + deadline.toSaveFormat();
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + deadline + ")";
    }
}
