package yuno.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import yuno.util.DateTimeFormats;

/**
 * Represents a task that must be completed by a specified deadline.
 */
public class Deadline extends Task {
    /** Stores the date and time by which the task must be completed. */
    private final LocalDateTime deadlineDateTime;

    /**
     * Creates a deadline task with the specified description, status, and deadline.
     *
     * @param description Text that describes the task.
     * @param isDone Whether the task is completed.
     * @param deadlineDateTime Date and time by which the task must be completed.
     */
    public Deadline(String description, boolean isDone, LocalDateTime deadlineDateTime) {
        super(description, isDone);
        assert deadlineDateTime != null : "Deadline date-time must not be null";
        this.deadlineDateTime = deadlineDateTime;
    }

    /**
     * Returns the date and time by which this task must be completed.
     *
     * @return Deadline date and time.
     */
    public LocalDateTime getDeadlineDateTime() {
        return deadlineDateTime;
    }

    @Override
    public boolean isRelevantFor(LocalDate date) {
        return !getDeadlineDateTime().toLocalDate().isAfter(date);
    }

    @Override
    public String toStorageString() {
        return String.format(
                "D | %c | %s | %s",
                getStatus(),
                getDescription(),
                DateTimeFormats.STORAGE_FORMATTER.format(getDeadlineDateTime()));
    }

    @Override
    public LocalDateTime getChronologicalTime() {
        return getDeadlineDateTime();
    }

    @Override
    Task copy() {
        return new Deadline(getDescription(), getStatus() == 'X', getDeadlineDateTime());
    }

    /**
     * Returns this deadline task in a display-ready format.
     *
     * @return Formatted deadline task description, status, and deadline.
     */
    @Override
    public String toString() {
        return String.format(
                "[D]%s (by: %s)",
                super.toString(),
                DateTimeFormats.DISPLAY_FORMATTER.format(getDeadlineDateTime()));
    }
}
