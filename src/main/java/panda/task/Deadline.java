package panda.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import panda.exception.InvalidDateException;

/**
 * Represents a task that must be completed by a stated date or time.
 *
 * The deadline is parsed when the task is created, ensuring that every stored
 * deadline has a valid date and time.
 */
public class Deadline extends Task {
    private LocalDateTime deadlineDateTime;

    /**
     * Creates an unfinished deadline task.
     *
     * @param name the task description.
     * @param deadlineDateTimeText the date and time text supplied by the user.
     * @throws InvalidDateException if {@code deadlineDateTimeText} is not a valid date and time.
     */
    public Deadline(String name, String deadlineDateTimeText) throws InvalidDateException {
        super(name);
        deadlineDateTime = parseDateTime(deadlineDateTimeText);
    }

    /**
     * Returns the deadline task type.
     *
     * @return the deadline task type.
     */
    @Override
    public TaskType getType() {
        return TaskType.DEADLINE;
    }

    /**
     * Changes this task's deadline.
     *
     * @param updatedDateTimeText the validated replacement date and time text.
     * @throws InvalidDateException if the replacement is not a valid date and time.
     */
    public void reschedule(String updatedDateTimeText) throws InvalidDateException {
        LocalDateTime updatedDateTime = parseDateTime(updatedDateTimeText);
        deadlineDateTime = updatedDateTime;
    }

    /**
     * Returns the task description together with its formatted deadline.
     *
     * @return the formatted deadline description.
     */
    @Override
    public String getDisplayText() {
        return getName() + " (by: " + formatDateTimeForDisplay(deadlineDateTime) + ")";
    }

    /**
     * Checks whether this deadline is due on the supplied date.
     *
     * @param date the date used to filter the task list.
     * @return true when the deadline falls on {@code date}.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return deadlineDateTime.toLocalDate().equals(date);
    }

    /**
     * Converts this deadline into one line of Panda's storage format.
     *
     * @return the common task fields followed by the deadline value.
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | "
                + escapeDataField(formatDateTimeForStorage(deadlineDateTime));
    }
}
