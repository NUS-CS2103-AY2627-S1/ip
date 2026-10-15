package stephen.task;

import java.time.LocalDate;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description description of the task
     * @param by deadline date
     */
    public Deadline(String description, LocalDate by) {
        super(TaskType.DEADLINE, description);
        this.by = by;
    }

    /**
     * Checks whether this deadline is due on the given date.
     *
     * @param date date to check
     * @return {@code true} if the date is the deadline date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    /**
     * Returns this deadline in the save-file format.
     *
     * @return serialized task data followed by the deadline date
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + by.format(INPUT_DATE_FORMAT);
    }

    /**
     * Returns this deadline in the chatbot display format.
     *
     * @return task details followed by the formatted deadline date
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
