package lobby.task;

/**
 * Represents a task that has user-supplied start and end date/time strings.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event.
     *
     * @param description the event description.
     * @param from the event start text supplied by the user.
     * @param to the event end text supplied by the user.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Formats this event as one line in the save file.
     *
     * @return the serialized event
     */
    @Override
    public String toDataString() {
        return "E | " + toDataFields()
                + " | " + escapeDataField(from)
                + " | " + escapeDataField(to);
    }

    /**
     * Formats this event for display in the task list.
     *
     * @return the task type, completion status, description, start, and end
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from + " to: " + to + ")";
    }
}
