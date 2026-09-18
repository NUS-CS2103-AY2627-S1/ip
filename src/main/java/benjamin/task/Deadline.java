package benjamin.task;

import java.time.LocalDate;

/** A task that has to be finished by a particular date, and possibly a time. */
public class Deadline extends Task {
    /** The letter that marks a deadline in the save file. */
    public static final String TYPE_LETTER = "D";

    /** When the task has to be finished by. */
    protected TaskDateTime by;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description what has to be finished.
     * @param by when it has to be finished by.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns true if the deadline falls on the given day, whatever time of
     * day it is due.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.getDate().equals(date);
    }

    @Override
    protected String identity() {
        return super.identity() + " | " + by.toStorageString();
    }

    /** Returns the save line, marked with the letter {@code D}. */
    @Override
    public String toSaveFormat() {
        return TYPE_LETTER + " | " + super.toSaveFormat() + " | " + by.toStorageString()
                + getTagsField();
    }

    /** Returns the deadline as shown to the user, with its due date in brackets. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")" + getTagSuffix();
    }
}
