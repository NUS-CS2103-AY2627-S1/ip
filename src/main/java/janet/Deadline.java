package janet;

import java.time.LocalDate;

/**
 * Represents a <code>Task</code> that must be completed by a specified deadline.
 */
public class Deadline extends Task {

    protected static final String DEADLINE_SEP = "/by";
    private static final String TASK_TYPE = "D";
    private final LocalDate deadline;

    /**
     * Creates a <code>Deadline</code> task type with the specified completion status, type, label, and deadline.
     * @throws JanetException If the task label is empty.
     */
    public Deadline(boolean isDone, String taskLabel, LocalDate deadline) throws JanetException {
        super(isDone, Deadline.TASK_TYPE, taskLabel);
        this.deadline = deadline;
    }

    @Override
    public Task markDone() throws JanetException {
        return new Deadline(true, super.taskLabel, this.deadline);
    }

    @Override
    public String toFileFormat() {
        return String.join(StorageTaskParser.INLINE_SEP, super.toFileFormat(), this.deadline.toString());
    }

    @Override
    public String toString() {
        return String.format(
                "%s (by: %s)",
                super.toString(),
                this.deadline.format(DateFormat.DATE_TIME_FORMAT)
        );
    }
}
