package milo.task;

/**
 * Represents a task that starts at a specific date/time and ends at another,
 * for example "team project meeting 2/10/2026 2-4pm".
 */
public class Event extends Task {
    /** When the event starts. Stored as free text, not parsed as a date. */
    protected String from;

    /** When the event ends. Stored as free text, not parsed as a date. */
    protected String to;

    /**
     * Creates an event.
     *
     * @param description what the event is about
     * @param from        when the event starts
     * @param to          when the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns an independent copy of this event.
     *
     * @return an event equal to this one but sharing no state with it
     */
    @Override
    public Event copy() {
        Event copy = new Event(description, from, to);
        copy.isDone = this.isDone;
        return copy;
    }

    /**
     * Returns the event tagged with its type and time span, for example
     * {@code [E][ ] project meeting (from: Mon 2pm to: 4pm)}.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns this task as one line of the save file.
     */
    @Override
    public String toFileFormat() {
        return "E" + FILE_SEPARATOR + super.toFileFormat()
                + FILE_SEPARATOR + from + FILE_SEPARATOR + to;
    }
}
