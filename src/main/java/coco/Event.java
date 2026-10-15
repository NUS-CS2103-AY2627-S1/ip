package coco;

/**
 * A task that spans a start and an end time.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates a new, not-yet-done event.
     *
     * @param description Description of the event.
     * @param from Start time of the event.
     * @param to End time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        // Parser.parseEvent is responsible for rejecting an empty
        // description/from/to before ever reaching this constructor.
        assert !description.isEmpty() : "description should already be validated by Parser";
        assert from != null && !from.isEmpty() : "from should already be validated by Parser";
        assert to != null && !to.isEmpty() : "to should already be validated by Parser";
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + from + " | " + to;
    }
}
