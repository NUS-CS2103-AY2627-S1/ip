package walnut;

import java.time.LocalDateTime;

/**
 * Represents a task that takes place during a specified time interval.
 */
public class Event extends Task {
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;

    /**
     * Creates an event with the specified description and time interval.
     */
    public Event(String description, LocalDateTime eventStartTime,
                 LocalDateTime eventEndTime) {
        super(description);
        if (eventStartTime == null
                || eventEndTime == null
                || !eventEndTime.isAfter(eventStartTime)) {
            throw new IllegalArgumentException(
                    "Event end time must be after start time"
            );
        }
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
    }

    public String getEventStartTime() {
        return this.eventStartTime.format(Parser.STORAGE_FMT);
    }

    public String getEventEndTime() {
        return this.eventEndTime.format(Parser.STORAGE_FMT);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + this.eventStartTime.format(Parser.DISPLAY_FMT)
                + " to: "
                + this.eventEndTime.format(Parser.DISPLAY_FMT)
                + ")";
    }
}
