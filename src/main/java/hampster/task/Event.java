package hampster.task;

import java.time.LocalDateTime;

import hampster.parser.DateTimeParser;

/**
 * Represents an event that occurs between a start time and an end time.
 */
public class Event extends Task {

    /** The event start time. */
    private final LocalDateTime from;

    /** The event end time. */
    private final LocalDateTime to;

    /**
     * Creates an event with the given completion status, description,
     * start time, and end time.
     *
     * @param done whether the event is completed
     * @param description the event description
     * @param from the event start time
     * @param to the event end time
     */
    public Event(
            boolean done,
            String description,
            LocalDateTime from,
            LocalDateTime to
    ) {
        super(done, description);
        validateTimes(from, to);
        this.from = from;
        this.to = to;
    }

    /** Creates an event with an optional tag. */
    public Event(
            boolean done,
            String description,
            LocalDateTime from,
            LocalDateTime to,
            String tag
    ) {
        super(done, description, tag);
        validateTimes(from, to);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an incomplete event.
     *
     * @param description the event description
     * @param from the event start time
     * @param to the event end time
     */
    public Event(
            String description,
            LocalDateTime from,
            LocalDateTime to
    ) {
        this(false, description, from, to);
    }

    /** Rejects missing or non-positive-length event intervals. */
    private static void validateTimes(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Event times must not be null");
        }
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("Event end time must be later than start time");
        }
    }

    /**
     * Converts the event into its pipe-separated storage format.
     *
     * @return the serialized event
     */
    @Override
    public String saveString() {
        return String.format(
            "E|%s|%s|%s|%s|%s",
                this.done ? "1" : "0",
                this.description,
                DateTimeParser.deparse(this.from),
            DateTimeParser.deparse(this.to),
            this.tag
        );
    }

    /**
     * Returns a user-readable representation of the event.
     *
     * @return the event status, description, start time, and end time
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimeParser.deparse(from)
                + " to: " + DateTimeParser.deparse(to) + ")";
    }
}
