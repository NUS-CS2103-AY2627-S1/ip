package erina.task;

import java.util.List;

/**
 * A task that spans a period of time, shown as
 * {@code [E][ ] project meeting (from: Mon 2pm to: 4pm)}.
 */
public class Event extends Task {
    /** When the event starts, kept as the text the user typed. */
    private final String from;

    /** When the event ends, kept as the text the user typed. */
    private final String to;

    /**
     * Creates an event that is not yet done.
     *
     * @param description what the event is
     * @param from        when it starts, as typed by the user
     * @param to          when it ends, as typed by the user
     */
    public Event(String description, String from, String to) {
        super(description);
        assert from != null && to != null : "an event needs both a start and an end";
        this.from = from;
        this.to = to;
    }

    @Override
    public String getTypeCode() {
        return "E";
    }

    @Override
    protected List<String> getExtraSaveFields() {
        return List.of(from, to);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
