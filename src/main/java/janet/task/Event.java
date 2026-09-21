package janet.task;

/**
 * Represents a task that takes place between a start and an end date or time.
 */
public class Event extends Task {
    /** The date or time when this event starts. */
    private String start;

    /** The date or time when this event ends. */
    private String end;

    /**
     * Creates a new incomplete event task.
     *
     * @param description the text describing the event.
     * @param start the event start date or time.
     * @param end the event end date or time.
     */
    public Event(String description, String start, String end) {
        super(description);
        TaskDateTime parsedStart = TaskDateTime.parse(start);
        TaskDateTime parsedEnd = TaskDateTime.parse(end);
        if (!parsedStart.hasSameKind(parsedEnd)) {
            throw new IllegalArgumentException("Event start and end must use the same date/time format.");
        }
        if (parsedEnd.isBefore(parsedStart)) {
            throw new IllegalArgumentException("Event end must not precede its start.");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the event start in the form it was supplied by the user.
     *
     * @return the unformatted event start
     */
    public String getStart() {
        return start;
    }

    /**
     * Returns the event end in the form it was supplied by the user.
     *
     * @return the unformatted event end
     */
    public String getEnd() {
        return end;
    }

    /**
     * Returns the icon identifying this task as an event.
     *
     * @return the event type icon
     */
    @Override
    public String getTypeIcon() {
        return "[E]";
    }

    /**
     * Returns the event description together with its time range.
     *
     * @return the description and event time range
     */
    @Override
    public String getDescription() {
        return description + " (from: " + start + " to: " + end + ")";
    }
}
