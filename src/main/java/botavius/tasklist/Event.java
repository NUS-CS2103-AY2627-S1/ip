package botavius.tasklist;

import java.time.LocalDateTime;

import botavius.exception.BotaviusException;

/** A task scheduled between textual start and end times. */
public class Event extends Task {

    /** Text describing when the event starts. */
    private LocalDateTime from;
    /** Text describing when the event ends. */
    private LocalDateTime to;

    /**
     * Creates an incomplete event task.
     *
     * @param description task description
     * @param from event start date and time in {@code dd-MM-yyyy HH:mm} format
     * @param to event end date and time in {@code dd-MM-yyyy HH:mm} format
     * @throws BotaviusException if either date-time is missing or invalid
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = TaskDateTime.parse(from, "event start time");
        this.to = TaskDateTime.parse(to, "event end time");
        if (this.from.isAfter(this.to)) {
            throw new BotaviusException("event start time must not be after event end time.");
        }
    }

    /** Returns the event start date and time.
     *
     * @return the event start
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event end date and time.
     *
     * @return the event end
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Formats the event task for display.
     *
     * @return task text with its start and end times
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + TaskDateTime.toDisplayString(from) + " to: "
                + TaskDateTime.toDisplayString(to) + ")";
    }

    /**
     * Returns the event in the format used when persisting it.
     *
     * @return the event's storage representation
     */
    public String toStorageString() {
        return "[E]" + super.toStorageString() + " from: "
                + TaskDateTime.toStorageString(from) + " to: "
                + TaskDateTime.toStorageString(to);
    }
}
