package cheecken;

/**
 * Represents a task that must be completed by a date or date-time.
 */
public class Deadline extends Task {
    private final DateTimeValue deadline;

    /**
     * Creates a deadline after parsing its date or date-time using the local clock.
     * @param task description of the task
     * @param deadline deadline date or date-time
     */
    public Deadline(String task, String deadline) {
        this(task, DateTimeValue.parse(deadline, "deadline"));
    }

    /**
     * Creates a deadline from a value already resolved by command handling or storage.
     * @param task description of the task
     * @param deadline deadline date or date-time
     */
    Deadline(String task, DateTimeValue deadline) {
        super(task);
        this.deadline = deadline;
    }

    /**
     * Returns the deadline display representation.
     * @return formatted task description and completion state
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + deadline.display() + ")";
    }

    /**
     * Serializes the resolved deadline, preserving whether a time was supplied.
     * @return task record formatted for persistence
     */
    @Override
    public String toStorageString() {
        return "D | " + (isMarked ? "1" : "0") + " | " + task + " | " + deadline.storage();
    }
}
