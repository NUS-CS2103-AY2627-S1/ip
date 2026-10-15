package grug.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Represents a task that needs to be done before a specific date/time
 * e.g. _submit report by 2026-12-25 17:00.
 */
public class DeadlineTask extends Task {
    private LocalDateTime deadlineDatetime;

    /**
     * Creates a new {@link DeadlineTask} that is incomplete.
     *
     * @param details  The details of the task.
     * @param deadline The deadline date/time of the task.
     * @throws DateTimeParseException If the deadline text cannot be parsed.
     */
    public DeadlineTask(String details, String deadline) {
        super(details);
        this.deadlineDatetime = LocalDateTime.parse(deadline, DATE_TIME_INPUT_FORMATTER);
    }

    public LocalDateTime getDeadlineDatetime() {
        return this.deadlineDatetime;
    }

    @Override
    public boolean doesOccurOn(LocalDate date) {
        return this.deadlineDatetime.toLocalDate().equals(date);
    }

    @Override
    public String toString() {
        return "[D]%s (by: %s)".formatted(
                super.toString(),
                deadlineDatetime.format(DATE_TIME_DISPLAY_FORMATTER));
    }
}
