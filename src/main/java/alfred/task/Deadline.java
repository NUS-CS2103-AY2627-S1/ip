package alfred.task;

import java.time.LocalDate;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** Date or time by which the task should be completed. */
    private final TaskDateTime deadline;

    /** Creates an incomplete deadline task. */
    public Deadline(String description, TaskDateTime deadline) {
        super(description);
        // Parser and save loading only construct a deadline after parsing a due date.
        assert deadline != null : "A deadline task must have a due date";
        this.deadline = deadline;
    }

    @Override
    protected TaskType getType() {
        return TaskType.DEADLINE;
    }

    @Override
    protected String getDateTimeText() {
        return " (by: " + deadline.toDisplayString() + ")";
    }

    @Override
    public String toSaveFormat() {
        return getType().getIcon() + " | " + getStatusBit() + " | " + description
                + " | " + deadline.toSaveString() + " | " + getArchiveBit();
    }

    /**
     * Returns {@code true} if this deadline's due date is {@code date}.
     *
     * @param date Calendar date to test.
     * @return {@code true} if the due date equals {@code date}.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return deadline.toLocalDate().equals(date);
    }
}
