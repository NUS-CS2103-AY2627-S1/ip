package shrek.task;

import java.util.Collection;

/**
 * Represents a task that starts and ends at specific date/times.
 */
public class Event extends Task {

    protected EventTime from;
    protected EventTime to;

    /**
     * Creates a new event task.
     *
     * @param description the text describing the task.
     * @param from        the start date/time of the event.
     * @param to          the end date/time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = EventTime.parse(from);
        this.to = EventTime.parse(to);
        validateOrder();
    }

    /**
     * Creates a new event task with tags.
     *
     * @param description the text describing the task.
     * @param from        the start date/time of the event.
     * @param to          the end date/time of the event.
     * @param tags        the initial tags for the task.
     */
    public Event(String description, String from, String to, Collection<String> tags) {
        super(description, tags);
        this.from = EventTime.parse(from);
        this.to = EventTime.parse(to);
        validateOrder();
    }

    @Override
    public String toSaveFormat() {
        return "E | " + (isDone ? "1" : "0") + " | " + description + " | " + from.toStorageFormat()
                + " | " + to.toStorageFormat()
                + getTagsStorageSuffix();
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    protected String getCanonicalDetails() {
        return super.getCanonicalDetails() + "|" + from.toCanonicalValue() + "|" + to.toCanonicalValue();
    }

    private void validateOrder() {
        if (from.hasDate() != to.hasDate()) {
            throw new IllegalArgumentException("Event start and end must use the same date/time format.");
        }
        if (from.compareTo(to) >= 0) {
            throw new IllegalArgumentException("Event end must be later than its start.");
        }
    }
}
