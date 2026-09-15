package nova;

import java.time.LocalDateTime;

/**
 * A task that must be finished by a particular date, such as
 * "return book (by: Oct 15 2019)".
 * The due date is stored as a LocalDateTime rather than text, so the format
 * the user types is independent of the format shown back to them.
 */
public class Deadline extends Task {
    protected final LocalDateTime by;

    /**
     * Creates a deadline due at the given time.
     *
     * @param taskName what the user typed as the description.
     * @param isMarked whether the task starts out done.
     * @param by       when the task is due; a midnight time means the user
     *                 gave a date only, and no time is displayed.
     */
    public Deadline(String taskName, boolean isMarked, LocalDateTime by) {
        super(taskName, isMarked);
        this.by = by;
    }

    /**
     * Returns this task as one data-file line.
     *
     * @return the saved form, e.g. "D | 0 | return book | 2019-10-15T18:00".
     *         The date is written in ISO-8601 so that it reads back exactly.
     */
    @Override
    public String toDataString() {
        return "D | " + dataFields() + " | " + this.by;
    }

    /**
     * Returns this task in the form shown to the user.
     *
     * @return the display form, e.g.
     *         "[D][ ] return book (by: Oct 15 2019, 6:00PM)".
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + String.format(" (by: %s)", formatDateTime(this.by));
    }
}
