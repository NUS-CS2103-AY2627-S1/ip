
package ip.model;

import ip.parser.ParsedDateTime;

/** Represents a task occurring between a start and end date or time. */
public class Event extends Task {

    protected ParsedDateTime from;
    protected ParsedDateTime to;

    /** Creates an event with a description, start, and end date or time. */
    public Event(String description, ParsedDateTime from, ParsedDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    /** Returns the serialized event representation. */
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from.toFileFormat() + " | " + to.toFileFormat();
    }

    @Override
    /** Returns the user-facing event representation. */
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
