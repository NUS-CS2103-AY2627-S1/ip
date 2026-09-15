package luigibot.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Represents a task that occurs during a specific time period.
 */
public class Event extends Task {
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an incomplete event with the given description and date-time period.
     *
     * @param description description of the event.
     * @param from date and time at which the event starts, in yyyy-MM-dd HHmm format.
     * @param to date and time at which the event ends, in yyyy-MM-dd HHmm format.
     * @throws DateTimeParseException if either date and time is invalid or uses the wrong format.
     * @throws IllegalArgumentException if the event does not end after it starts.
     */
    public Event(String description, String from, String to) {
        super(description);
        LocalDateTime parsedFrom = LocalDateTime.parse(from, INPUT_FORMAT);
        LocalDateTime parsedTo = LocalDateTime.parse(to, INPUT_FORMAT);
        if (!parsedTo.isAfter(parsedFrom)) {
            throw new IllegalArgumentException("Event end must be after its start");
        }
        this.from = parsedFrom;
        this.to = parsedTo;
    }

    /**
     * Returns the event type icon, task details, and time period.
     *
     * @return display form of this event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + this.from.format(DISPLAY_FORMAT)
                + " to: " + this.to.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns this event in the text format used by the save file.
     *
     * @return save-file representation of this event.
     */
    @Override
    public String toFileString() {
        return getFileString("E") + " | " + this.from.format(INPUT_FORMAT)
                + " | " + this.to.format(INPUT_FORMAT);
    }

    /**
     * Returns whether any part of this event occurs on the given date.
     *
     * @param date date to check.
     * @return true when the event overlaps the given date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();
        return this.from.isBefore(startOfNextDay) && this.to.isAfter(startOfDay);
    }

    /**
     * Returns whether this event shares any time with another event.
     * Events that meet exactly at an endpoint do not overlap.
     *
     * @param other event to compare with.
     * @return true when the two events share a time period.
     */
    public boolean overlaps(Event other) {
        assert other != null : "event to compare should not be null";

        return this.from.isBefore(other.to) && this.to.isAfter(other.from);
    }
}
