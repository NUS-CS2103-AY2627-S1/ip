package chudgpt.task;

import java.time.LocalDate;

/** A task that must be completed by a specified time. */
public class Deadline extends Task {
    /** Date by which this task should be completed. */
    protected final LocalDate submitBy;

    /**
     * Creates a deadline task with the specified description and due date.
     *
     * @param description task description.
     * @param submitBy date by which the task should be completed.
     */
    public Deadline(String description, LocalDate submitBy) {
        super(description);
        assert submitBy != null : "Deadline date should not be null";
        this.submitBy = submitBy;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && submitBy.equals(((Deadline) other).submitBy);
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), submitBy);
    }

    /**
     * Converts a task to its representation in the save file.
     *
     * @return representation of the task in the save file.
     */
    @Override
    public String toSaveMessage() {
        return String.format("D | %d | %s | %s | %s", isCompleted ? 1 : 0, getPriority(), description, submitBy);
    }
}
