package orbit;

/**
 * A task that takes place between specified start and end times.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event spanning the supplied times.
     *
     * @param description Task description.
     * @param from Event start.
     * @param to Event end.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected TaskType getType() {
        return TaskType.EVENT;
    }

    @Override
    public boolean isDuplicateOf(Task other) {
        return other instanceof Event event && super.isDuplicateOf(other)
                && from.strip().equalsIgnoreCase(event.from.strip())
                && to.strip().equalsIgnoreCase(event.to.strip());
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + from + " | " + to;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
