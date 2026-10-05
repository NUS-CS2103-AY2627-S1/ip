package friday.task;

import java.time.LocalDateTime;
import java.util.Optional;

import friday.FridayException;

/**
 * Represents a task that needs to be done by a specific date or time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description description of the deadline task
     * @param by deadline date and time
     * @throws FridayException if the deadline date and time cannot be parsed
     */
    public Deadline(String description, String by) throws FridayException {
        super(description);
        this.by = TaskDateTime.parse(by);
    }

    /**
     * Returns the icon that shows this task is a deadline.
     *
     * @return deadline type icon
     */
    @Override
    public String getTaskTypeIcon() {
        return "D";
    }

    @Override
    protected Task createNextOccurrence(RepeatFrequency frequency) throws FridayException {
        return new Deadline(description, TaskDateTime.formatForFile(frequency.advance(by)));
    }

    /**
     * Returns this deadline's due date and time.
     *
     * @return deadline date and time
     */
    @Override
    public Optional<LocalDateTime> getReminderDateTime() {
        return Optional.of(by);
    }

    /**
     * Returns this deadline in the format used by the save file.
     *
     * @return save-file representation of the deadline
     */
    @Override
    public String toFileString() {
        return appendRepeatFrequency(getBaseFileString() + " | " + TaskDateTime.formatForFile(this.by));
    }

    /**
     * Returns this deadline as text for display to the user.
     *
     * @return user-facing representation of the deadline
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + TaskDateTime.formatForDisplay(this.by) + ")";
    }
}
