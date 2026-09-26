package amadeus.task;

import java.time.LocalDate;

/** A task that has to be finished by a particular date and time. */
public class Deadline extends Task {

    /** When the task is due. */
    protected TaskDateTime by;

    /**
     * Creates a deadline that starts off not done.
     *
     * @param description what has to be done, e.g. "return book".
     * @param by          when it is due; already parsed, so it cannot be a date the app
     *                    does not understand.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description);
        this.by = by;
    }

    /** Returns the date the task is due. */
    public TaskDateTime getBy() {
        return by;
    }

    /** A deadline is relevant to the day it falls due on. */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.isOn(date);
    }

    /**
     * Returns the deadline as the user sees it, e.g.
     * "[D][ ] return book (by: Oct 15 2019)".
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
