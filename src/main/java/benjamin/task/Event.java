package benjamin.task;

import java.time.LocalDate;

import benjamin.BenjaminException;

/** A task that runs from one point in time to another. */
public class Event extends Task {
    /** The letter that marks a event in the save file. */
    public static final String TYPE_LETTER = "E";

    /** When the event starts. */
    protected TaskDateTime from;

    /** When the event ends. */
    protected TaskDateTime to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description what the event is.
     * @param from when it starts.
     * @param to when it ends.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to)
            throws BenjaminException {
        super(description);

        if (!to.isAfter(from)) {
            throw new BenjaminException("An event has to finish after it starts, and "
                    + to + " does not come after " + from + ".");
        }

        this.from = from;
        this.to = to;
    }

    /**
     * Returns true if the given day is covered by the event.
     *
     * <p>An event covers every day from its start date to its end date, and
     * both of those days count as covered.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.getDate()) && !date.isAfter(to.getDate());
    }

    @Override
    protected String identity() {
        return super.identity() + " | " + from.toStorageString()
                + " | " + to.toStorageString();
    }

    /** Returns the save line, marked with the letter {@code E}. */
    @Override
    public String toSaveFormat() {
        return TYPE_LETTER + " | " + super.toSaveFormat() + " | " + from.toStorageString()
                + " | " + to.toStorageString() + getTagsField();
    }

    /** Returns the event as shown to the user, with both ends in brackets. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")"
                + getTagSuffix();
    }
}
