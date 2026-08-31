/**
 * Represents a task with start and end time strings.
 */
public class Event extends Task {
    /** Shown when any of the three pieces of event text is missing. */
    private static final String MISSING_FIELD_MESSAGE =
            "An event needs a description, /from time, and /to time.";

    private final String from;
    private final String to;

    /**
     * Creates an event with a description, starting time, and ending time.
     *
     * @param description text describing the event
     * @param from the event start time, stored exactly as entered
     * @param to the event end time, stored exactly as entered
     * @throws BibiException if the description, start time, or end time is blank
     */
    public Event(String description, String from, String to) throws BibiException {
        super(requireTaskText(description, MISSING_FIELD_MESSAGE));
        this.from = requireTaskText(from, MISSING_FIELD_MESSAGE);
        this.to = requireTaskText(to, MISSING_FIELD_MESSAGE);
    }

    @Override
    protected String getTypeCode() {
        return "E";
    }

    @Override
    protected String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns the start and end times as the two extra save-file fields.
     */
    @Override
    protected String getSaveFields() {
        return " " + FIELD_SEPARATOR + " " + from + " " + FIELD_SEPARATOR + " " + to;
    }
}
