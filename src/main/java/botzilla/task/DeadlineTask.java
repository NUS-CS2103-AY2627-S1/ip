package botzilla.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import botzilla.BotzillaException;

/**
 * Represents a task that must be completed by a specific date/time,
 * e.g. "return book /by 2/12/2019 1800". If the given date text can't
 * be parsed, it is stored and displayed as plain text instead.
 */
public class DeadlineTask extends Task {
    private final FlexibleDateTime by;

    /**
     * Constructs a new DeadlineTask, attempting to parse the given
     * deadline text as a date or date-time. Falls back to storing it
     * as raw text if it isn't date-shaped at all.
     *
     * @param name Display name/description of the task.
     * @param by Deadline text, e.g. "2/12/2019 1800" or free text.
     * @throws BotzillaException if {@code by} is date-shaped but names an
     *                           invalid calendar date or time (see {@link DateTimeUtil#parse}).
     */
    public DeadlineTask(String name, String by) throws BotzillaException {
        super(name, TaskType.DEADLINE);
        this.by = FlexibleDateTime.parse(by);
    }

    /**
     * Returns this task, including its deadline, serialized for saving
     * to disk, with an appended "| deadline" field.
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + by.toFileString() + tagsFileSuffix();
    }

    /**
     * Returns a human-readable representation including the deadline and
     * any tags, e.g. "[D][ ] return book (by: Dec 02 2019, 6:00PM) #fun".
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.toDisplayString() + ")" + tagsDisplaySuffix();
    }

    /**
     * Returns the parsed deadline as a LocalDateTime, or null if the
     * original input couldn't be parsed as a date.
     */
    public LocalDateTime getBy() {
        return by.toDateTimeOrNull();
    }

    /**
     * Returns the calendar date of this deadline, or empty if the
     * original input couldn't be parsed as a date.
     */
    @Override
    public Optional<LocalDate> getDate() {
        return Optional.ofNullable(getBy()).map(LocalDateTime::toLocalDate);
    }

    /**
     * Returns this task's duplicate-detection signature, including its
     * deadline field alongside the base type/name signature.
     */
    @Override
    String duplicateSignature() {
        return super.duplicateSignature() + "|" + by.toFileString();
    }
}
