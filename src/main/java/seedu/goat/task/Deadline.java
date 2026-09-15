package seedu.goat.task;

import java.time.LocalDate;

import seedu.goat.DateFormats;

/**
 * A task that must be finished by a stated date, such as
 * {@code deadline return book /by 2019-12-02}.
 */
public class Deadline extends Task {

    /** The date the task is due. */
    private final LocalDate by;

    /**
     * Creates a deadline that starts out not done.
     *
     * @param description what the user wants to do.
     * @param by the date it is due.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    public boolean isOn(LocalDate date) {
        return by.equals(date);
    }

    @Override
    public boolean isDuplicateOf(Task other) {
        // The cast is safe because the base check has already established that both
        // tasks are deadlines.
        return super.isDuplicateOf(other) && by.equals(((Deadline) other).by);
    }

    @Override
    public String toFileString() {
        // LocalDate.toString() is the ISO form, which is exactly what parse() reads back.
        return "D | " + getFileStatus() + " | " + description + " | " + by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateFormats.format(by) + ")";
    }
}
