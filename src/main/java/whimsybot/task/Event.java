package whimsybot.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Represents a task that occurs from one specified time to another. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final String legacyFrom;
    private final String legacyTo;

    /**
     * Creates an event with the given description and start and end values.
     *
     * @param description the text describing the event
     * @param from the event start
     * @param to the event end
     */
    public Event(String description, String from, String to) {
        super(description);
        this.fromDate = parseDate(from);
        this.toDate = parseDate(to);
        this.legacyFrom = fromDate == null ? from : null;
        this.legacyTo = toDate == null ? to : null;
        // Each endpoint uses exactly one representation: a parsed date or legacy text.
        assert (fromDate == null) != (legacyFrom == null) : "Event start representation is inconsistent";
        assert (toDate == null) != (legacyTo == null) : "Event end representation is inconsistent";
    }

    /** Returns the event start text for persistence. */
    public String getFrom() {
        return fromDate == null ? legacyFrom : fromDate.toString();
    }

    /** Returns the event end text for persistence. */
    public String getTo() {
        return toDate == null ? legacyTo : toDate.toString();
    }

    @Override
    public String toString() {
        String fromDisplay = fromDate == null ? legacyFrom : fromDate.format(DISPLAY_FORMAT);
        String toDisplay = toDate == null ? legacyTo : toDate.format(DISPLAY_FORMAT);
        return "[E]" + super.toString() + " (from: " + fromDisplay + " to: " + toDisplay + ")";
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
