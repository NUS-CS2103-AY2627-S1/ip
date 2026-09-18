package aster.task;

import java.time.LocalDate;

/**
 * A task that must be done by a stated date.
 *
 * <p>The due date is held as a real date rather than as the text the user typed, so
 * Aster can tell a date from any other wording and can show it in a friendlier form
 * than the one it is typed in.
 */
public class Deadline extends Task {
    private final LocalDate by;

    /**
     * Creates a deadline with the given description and due date, initially not done.
     *
     * @param description text describing the task.
     * @param by the date the task is due by.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the due date of this deadline.
     *
     * @return the date the task is due by.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns this deadline as its type marker, the inherited status and
     * description, and the due date, for example
     * {@code [D][ ] return book (by: Jun 06 2019)}.
     *
     * @return the display form of this deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + TaskDates.format(by) + ")";
    }
}
