package xerxes.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a task scheduled over a start and end date.
 */
public class Event extends Task {
    /** Format used when displaying event dates to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /** Date on which this event begins. */
    private final LocalDate startTime;

    /** Date on which this event ends. */
    private final LocalDate endTime;

    /**
     * Creates an incomplete event task.
     *
     * @param taskName Description of the event.
     * @param startTime Date on which the event starts.
     * @param endTime Date on which the event ends.
     */
    public Event(String taskName, LocalDate startTime, LocalDate endTime) {
        super(taskName);
        assert startTime != null : "An event must have a start date";
        assert endTime != null : "An event must have an end date";
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Eh, an event cannot end before it starts ah.");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the event start date.
     *
     * @return Start date of the event.
     */
    public LocalDate getStartTime() {
        return this.startTime;
    }

    /**
     * Returns the event end date.
     *
     * @return End date of the event.
     */
    public LocalDate getEndTime() {
        return this.endTime;
    }

    /**
     * Checks whether another event has the same description and date range.
     *
     * @param other Object to compare with this event.
     * @return True if both objects represent equivalent events.
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }

        Event event = (Event) other;
        return startTime.equals(event.startTime) && endTime.equals(event.endTime);
    }

    /**
     * Returns a hash code based on the event's task details.
     *
     * @return Hash code for this event.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startTime, endTime);
    }

    /**
     * Returns a display representation including this event's duration.
     *
     * @return Formatted event task description.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + this.startTime.format(DISPLAY_FORMAT) + " to: "
                + this.endTime.format(DISPLAY_FORMAT) + " )";
    }

}
