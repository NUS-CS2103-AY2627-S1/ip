package rex.task;

import java.time.LocalDate;

/** A task that must be finished by a particular date, and optionally by a time on that date. */
public class Deadline extends Task {
    private final TaskDateTime by;

    /**
     * Creates a deadline.
     *
     * @param description what has to be done.
     * @param by when it has to be done by.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description);
        this.by = by;
    }

    /** Returns "D", the letter marking a deadline in a listed or saved task. */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /** Returns the due date in brackets, e.g. " (by: Oct 15 2019)". */
    @Override
    public String getDetails() {
        return " (by: " + by + ")";
    }

    /** A deadline falls on the day it is due, whatever time of day that is. */
    @Override
    public boolean isOn(LocalDate date) {
        return by.isOn(date);
    }

    /**
     * Note that this writes the date in its save format, not the one shown to
     * the user: the file has to be read back, so it keeps the format that
     * TaskDateTime.parse understands rather than the prettier display one.
     */
    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + FIELD_SEPARATOR + by.toSaveFormat();
    }
}
