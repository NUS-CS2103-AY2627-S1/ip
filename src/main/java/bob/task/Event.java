package bob.task;

import java.time.temporal.TemporalAccessor;

import bob.parser.DateTimeParser;

/**
 * Represents a task scheduled between a start and end date or date-time.
 */
public class Event extends Task {
    private final TemporalAccessor from;
    private final TemporalAccessor to;

    /**
     * Creates an event task and parses its start and end values.
     *
     * @param description The event description.
     * @param from Start in dd/MM/yyyy or dd/MM/yyyy HHmm format.
     * @param to End in dd/MM/yyyy or dd/MM/yyyy HHmm format.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = DateTimeParser.parse(from);
        this.to = DateTimeParser.parse(to);
    }

    /**
     * Returns the parsed event start.
     *
     * @return Start stored as LocalDate or LocalDateTime.
     */
    public TemporalAccessor getFrom() {
        return from;
    }

    /**
     * Returns the parsed event end.
     *
     * @return End stored as LocalDate or LocalDateTime.
     */
    public TemporalAccessor getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimeParser.formatForDisplay(from)
                + " to: " + DateTimeParser.formatForDisplay(to) + ")";
    }
}
