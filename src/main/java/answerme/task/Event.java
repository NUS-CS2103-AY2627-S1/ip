package answerme.task;

import java.time.LocalDateTime;
import java.util.Objects;

import answerme.parser.DateTimeParser;

/**
 * Represents a task that occurs between a specified start and end time.
 */
public class Event extends Task {
    protected LocalDateTime eventStart;
    protected LocalDateTime eventEnd;

    /**
     * Creates an event task with the specified description and duration.
     *
     * @param description The event task description.
     * @param eventStart The event's start date and time.
     * @param eventEnd The event's end date and time.
     */
    public Event(String description, LocalDateTime eventStart, LocalDateTime eventEnd) {
        super(description);
        assert eventStart != null;
        assert eventEnd != null;

        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the event's fields in a human-readable format.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + DateTimeParser.formatDateTime(eventStart)
                + " to: " + DateTimeParser.formatDateTime(eventEnd) + ")";
    }

    /**
     * {@inheritDoc}
     *
     * Returns the event in storage format.
     */
    @Override
    public String toStorageFormat() {
        return getLogFormat("Event") + " | " + DateTimeParser.formatDateTime(eventStart)
                + " | " + DateTimeParser.formatDateTime(eventEnd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Event other) {
            return description.equals(other.description)
                    && eventStart.equals(other.eventStart)
                    && eventEnd.equals(other.eventEnd);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(description, eventStart, eventEnd);
    }
}
