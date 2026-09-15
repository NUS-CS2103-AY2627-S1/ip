package tianyi.task;

/**
 * Represents a task that must be completed by a specific date and optional time.
 */
public class Deadline extends Task {
    /**
     * Storage and display marker identifying a deadline.
     */
    public static final String TYPE_MARKER = "D";

    private final TaskTime deadline;

    /**
     * Creates an incomplete deadline with its due date and optional time.
     *
     * @param description Description of the deadline.
     * @param deadline Date and optional time by which the task is due.
     */
    public Deadline(String description, TaskTime deadline) {
        super(description);

        this.deadline = deadline;
    }

    /**
     * Reports whether this deadline remains due on the specified date.
     *
     * @param taskTime Date to check.
     * @return {@code true} if the deadline is on or after the specified date.
     */
    @Override
    public boolean isOccurringOn(TaskTime taskTime) {
        return !deadline.isBefore(taskTime);
    }

    /**
     * Serializes this deadline for storage.
     *
     * @return Storage representation including the deadline type and due date.
     */
    @Override
    public String getData() {
        return TYPE_MARKER + DATA_SEPARATOR + super.getData()
                + DATA_SEPARATOR + deadline.getData();
    }

    /**
     * Formats this deadline for display.
     *
     * @return User-facing representation including its due date and optional time.
     */
    @Override
    public String toString() {
        return "[" + TYPE_MARKER + "]" + super.toString() + " (by: " + deadline + ")";
    }
}
