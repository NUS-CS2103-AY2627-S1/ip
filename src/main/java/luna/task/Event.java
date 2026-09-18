package luna.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task that takes place within a time range.
 */
public class Event extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd MMM uuuu, h:mm a",
                    Locale.ENGLISH);
    private final LocalDateTime fromDateTime;
    private final LocalDateTime toDateTime;

    /**
     * Creates an event task with the given description and time range.
     *
     * @param description Description of the event task.
     * @param fromDateTime Start time of the event.
     * @param toDateTime End time of the event.
     */
    public Event(String description, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        super(description);
        this.fromDateTime = Objects.requireNonNull(fromDateTime, "An event start time must not be null.");
        this.toDateTime = Objects.requireNonNull(toDateTime, "An event end time must not be null.");
        if (!fromDateTime.isBefore(toDateTime)) {
            throw new IllegalArgumentException("An event must end after it starts.");
        }
    }

    /**
     * Returns the start time text of this event.
     *
     * @return Start time text.
     */
    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    /**
     * Returns the end time text of this event.
     *
     * @return End time text.
     */
    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

    /**
     * Returns whether another event has the same description and time range.
     *
     * @param other Task to compare with this event.
     * @return {@code true} if both events have the same details.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && fromDateTime.equals(((Event) other).fromDateTime)
                && toDateTime.equals(((Event) other).toDateTime);
    }

    /**
     * Returns the save-file representation of this event task.
     *
     * @return Serialized representation used in storage.
     */
    @Override
    public String toStorageString() {
        return "E | " + getStorageStatus() + " | " + getDescription()
                + " | " + fromDateTime + " | " + toDateTime;
    }

    /**
     * Returns the user-facing text representation of this event task.
     *
     * @return Formatted event text for display.
     */
    @Override
    public String toString() {
        return "[E]" + getStatusIcon() + " "
                + getDescription()
                + " (from: " + fromDateTime.format(OUTPUT_FORMAT)
                + " to: " + toDateTime.format(OUTPUT_FORMAT) + ")";
    }
}
