package jassabot.task;

import java.time.LocalDateTime;

import jassabot.util.DateTimeFormats;

/**
 * Represents an activity occurring between two dates and times.
 */
public class Event extends Task {

    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    /**
     * Creates an event task.
     *
     * @param description Activity that will take place.
     * @param startDateTime Date and time at which the event starts.
     * @param endDateTime Date and time at which the event ends.
     */
    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Converts this event into one line suitable for saving to the storage file.
     *
     * @return Storage-file representation of this event.
     */
    @Override
    public String toDataString() {
        return super.toDataString("E") + " | "
                + DateTimeFormats.formatForStorage(startDateTime)
                + " | " + DateTimeFormats.formatForStorage(endDateTime);
    }

    /**
     * Returns the type, completion status, description, and time range used to display this event.
     *
     * @return Display representation of this event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeFormats.formatForDisplay(startDateTime) + " to: "
                + DateTimeFormats.formatForDisplay(endDateTime) + ")";
    }
}
