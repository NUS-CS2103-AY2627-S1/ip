package pan;

import java.time.LocalDateTime;

/**
 * Represents an event: a task that runs from a start date/time to an end
 * date/time.
 */
public class Event extends Task {

    /** When the event starts. */
    private LocalDateTime start;

    /** When the event ends. */
    private LocalDateTime end;

    /**
     * Complaint when an event's two times are the wrong way round. Held as a
     * constant because both the {@code event} command and {@code update} can
     * put them that way, and the two must not drift into saying it
     * differently.
     */
    private static final String TIMES_OUT_OF_ORDER =
            " Ehh? PanPan can't make an event that finishes before it even starts~ "
            + "check the /from and /to? (・・;)";

    /** Creates an event with the given description, running from {@code start} to {@code end}. */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        assert start != null && end != null
                : "start/end should never be null: Parser.parseDateTime()/parseStoredDateTime() "
                + "always return a value or throw, never null";
        this.start = start;
        this.end = end;
    }

    @Override
    public void applyUpdate(String option, String value) throws PanException {
        // Each new time is parsed and checked against the one that is staying
        // BEFORE it is assigned, so a rejected update leaves the event as it
        // was rather than half-changed.
        switch (option) {
        case "from":
            LocalDateTime newStart = Parser.parseDateTime(value);
            requireInOrder(newStart, end);
            start = newStart;
            break;
        case "to":
            LocalDateTime newEnd = Parser.parseDateTime(value);
            requireInOrder(start, newEnd);
            end = newEnd;
            break;
        default:
            super.applyUpdate(option, value);
        }
    }

    /**
     * Rejects a pair of times that would make an event end before it begins.
     * Equal times are allowed - a zero-length event is odd but not wrong.
     *
     * @param start when the event would start.
     * @param end   when it would finish.
     * @throws PanException if {@code end} falls before {@code start}.
     */
    static void requireInOrder(LocalDateTime start, LocalDateTime end) throws PanException {
        if (end.isBefore(start)) {
            throw new PanException(TIMES_OUT_OF_ORDER);
        }
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + Parser.formatDateTime(start)
                + " to: " + Parser.formatDateTime(end) + ")";
    }

    /**
     * Returns the save-file line for an event, e.g.
     * {@code "E | 0 | project meeting | 2019-10-15T14:00 | 2019-10-15T16:00"}.
     * Start and end are stored as separate ISO-8601 fields so loading maps
     * straight back to the {@code Event(description, start, end)} constructor.
     */
    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + start + " | " + end;
    }
}
