package serina.task;

import java.time.LocalDate;

import serina.parser.DateParser;

/**
 * Represents a task that needs to be done by a specific date or time.
 */
public class Deadline extends Task {
    private final LocalDate deadlineDate;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description Text describing the task.
     * @param deadlineDate Deadline for the task.
     */
    public Deadline(String description, LocalDate deadlineDate) {
        this(description, deadlineDate, TaskStatus.NOT_DONE);
    }

    /**
     * Creates a deadline task with the given description, deadline, and saved status.
     *
     * @param description Text describing the task.
     * @param deadlineDate Deadline for the task.
     * @param status Saved completion status.
     */
    public Deadline(String description, LocalDate deadlineDate, TaskStatus status) {
        super(description, status);
        assert deadlineDate != null : "Deadline dates should be parsed before deadline construction.";

        this.deadlineDate = deadlineDate;
    }

    /**
     * Returns the deadline task type.
     *
     * @return {@link TaskType#DEADLINE}.
     */
    @Override
    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    @Override
    public Task copy() {
        return new Deadline(getDescription(), deadlineDate, getStatus());
    }

    /**
     * Returns the deadline date formatted for display.
     *
     * @return Display text containing the deadline date.
     */
    @Override
    protected String getDetails() {
        return " (by: " + DateParser.formatDisplayDate(deadlineDate) + ")";
    }

    /**
     * Returns the deadline date field used in the save file.
     *
     * @return Serialized deadline details.
     */
    @Override
    protected String getFileDetails() {
        return " | " + DateParser.formatFileDate(deadlineDate);
    }

    /**
     * Checks whether this task is due on the given date.
     *
     * @param date Date to check.
     * @return {@code true} if the deadline is on {@code date}.
     */
    @Override
    public boolean isOccurringOn(LocalDate date) {
        assert date != null : "Date searches should provide a date to compare against.";
        return deadlineDate.equals(date);
    }
}
