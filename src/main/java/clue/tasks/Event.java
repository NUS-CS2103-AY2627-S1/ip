package clue.tasks;

import java.time.LocalDateTime;

/** An Event, storing a {@link from} and {@link to} time in addition to the base class. */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /** Constructs an Event with the given name and from and to times. */
    public Event(String name, LocalDateTime from, LocalDateTime to) {
        super(name);
        this.from = validateDateTime(from, "Event start");
        this.to = validateDateTime(to, "Event end");
        validateEventRange(this.from, this.to);
    }

    /**
     * Returns the start time of this event.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the end time of this event.
     */
    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from %s to %s)", super.toString(),
                displayDate(this.from), displayDate(this.to));
    }

    @Override
    public String save(String separator) {
        return String.join(separator, "E", this.name,
                this.from.toString(), this.to.toString());
    }

    /** Constructs an Event object using an array of string arguments, serialized from {@link save} */
    public static Task construct(String... args) {
        if (args.length != 4) {
            throw new InvalidTaskException("Event requires exactly 4 arguments, got " + args.length);
        }
        LocalDateTime from = tryParseDate(args[2]);
        LocalDateTime to = tryParseDate(args[3]);
        validateEventRange(from, to);
        return new Event(args[1], from, to);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Event && ((Event) obj).name.equals(this.name)
                && ((Event) obj).from.equals(this.from) && ((Event) obj).to.equals(this.to);
    }

    /**
     * Validates that the datetime is a valid date/time.
     */
    private static LocalDateTime validateDateTime(LocalDateTime dt, String fieldName) {
        return Task.validateDate(dt, fieldName);
    }

    /**
     * Validates that the event start time is before the end time.
     *
     * @param from start time
     * @param to end time
     * @throws InvalidTaskException if from is not before to
     */
    private static void validateEventRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new InvalidTaskException("Event dates cannot be null");
        }
        if (!from.isBefore(to)) {
            throw new InvalidTaskException("Event start time must be before end time. "
                    + "Got: start=" + displayDate(from) + ", end=" + displayDate(to));
        }
    }
}
