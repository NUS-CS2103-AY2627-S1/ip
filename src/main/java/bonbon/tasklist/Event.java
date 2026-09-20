package bonbon.tasklist;

import bonbon.parser.Parser;

import java.time.LocalDateTime;


/**
 * Event is a type of task with a start and end date.
 * The start and end dates are represented in <code>datetime</code>.
 */
public class Event extends Task {
    private LocalDateTime start;
    private LocalDateTime end;
    private String place;

    /**
     * Creates an Event.
     *
     * @param name the name of the task.
     * @param start the date and time of the start, written in format 'yyyy-MM-dd HHmm'.
     * @param end the date and time of the end, written in format 'yyyy-MM-dd HHmm'.
     */
    public Event(String name, LocalDateTime start, LocalDateTime end, String place) {
        super(name);
        this.start = start;
        this.end = end;
        this.place = place;
    }

    /**
     * Returns <code>String</code> representation of event.
     *
     * @return <code>String</code> representation of event.
     */
    @Override
    public String toString() {
        return String.format("[E]%s (FROM: %s TO: %s AT: %s)", super.toString(),
                start.format(OUTPUT_FORMATTER), end.format(OUTPUT_FORMATTER), place);
    }

    /**
     * Returns the string representation of the deadline task formatted for storage persistence.
     *
     * @return The formatted string suitable for saving to a file.
     */
    @Override
    public String toFileFormat() {
        return String.format("E | %s | %s | %s | %s", super.toFileFormat(), start.format(Parser.INPUT_FORMATTER),
                end.format(Parser.INPUT_FORMATTER), place);
    }
}
