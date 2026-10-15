package probe.task;

import java.time.LocalDateTime;

import probe.parser.DateTimeParser;

/**
 * Represents a task occurring between two specified times.
 */
public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates an event with a description, start time, and end time.
     *
     * @param description Description of the event.
     * @param from Starting time of the event.
     * @param to Ending time of the event.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event in a display-friendly format.
     *
     * @return Display representation of the event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeParser.formatForDisplay(from) + " to: "
                + DateTimeParser.formatForDisplay(to) + ")";
    }
    
    /**
     * Returns the event in storage format.
     *
     * @return Storage representation of the event.
     */
    @Override
    public String toFileFormat() {
        String tagsText = tagsToFileFormat();
        return String.format("E | %d | %s | %s | %s%s", isDone ? 1 : 0, description,
                DateTimeParser.formatForStorage(from), DateTimeParser.formatForStorage(to),
                tagsText.isEmpty() ? "" : " | " + tagsText);
    }
}
