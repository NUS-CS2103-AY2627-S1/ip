package alfred.task;

import java.time.LocalDate;
import java.util.List;

import alfred.Dates;

/**
 * Represents a task that has to be finished by a stated date, for example
 * {@code [D][ ] return book (by: Oct 15 2019)}.
 */
public class Deadline extends Task {
    private final LocalDate by;

    /**
     * Creates a deadline that starts out not done.
     *
     * @param description what the user has to do.
     * @param by the day it has to be done by.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    public List<String> toFileFields() {
        return buildFileFields("D", by.toString());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Two deadlines are the same task only if they are due on the same day.
     */
    @Override
    public boolean isSameTask(Task other) {
        // The cast is safe only because super.isSameTask() checks the class first.
        return super.isSameTask(other) && by.equals(((Deadline) other).by);
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + Dates.format(by) + ")";
    }
}
