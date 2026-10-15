package taterror.task;

/**
 * A task that spans a start and end time.
 */
public class Event extends Task {
    private final String fromRaw;
    private final String toRaw;

    /**
     * Creates an event with the given description, start, and end.
     */
    public Event(String description, String fromRaw, String toRaw) {
        super(description);
        this.fromRaw = fromRaw;
        this.toRaw = toRaw;
    }

    public String getFromRaw() {
        return fromRaw;
    }

    public String getToRaw() {
        return toRaw;
    }

    @Override
    public String getTypeCode() {
        return "E";
    }

    @Override
    public String toSaveDetail() {
        return " | " + fromRaw + " | " + toRaw;
    }

    @Override
    public String toDisplayDetail() {
        return "(from: " + fromRaw + " to: " + toRaw + ")";
    }
}
