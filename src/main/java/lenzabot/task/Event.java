package lenzabot.task;

import java.time.LocalDateTime;

import lenzabot.parser.DateTimeParser;

/**
 * Represents a task that takes place between two date-time values.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event task with the given description, start, and end.
     *
     * @param description Description of what the event involves.
     * @param from Date and time when the event starts.
     * @param to Date and time when the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return String.format(
                "%s (from: %s to: %s)",
                super.toString(),
                DateTimeParser.format(this.from),
                DateTimeParser.format(this.to)
        );
    }

    @Override
    public String toSaveFormat() {
        return String.join(
                SAVE_FILE_SEPARATOR,
                super.toSaveFormat(),
                this.from.toString(),
                this.to.toString()
        );
    }
}
