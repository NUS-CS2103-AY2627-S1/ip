package daddy.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a task with a specified start and end date or time. */
public class Event extends Task {
    /** Formats event date-and-time values for display in the task list. */
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy HH:mm");
    /** Stores the event start date and time. */
    private final LocalDateTime from;
    /** Stores the event end date and time. */
    private final LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description the task description
     * @param from the event start date and time
     * @param to the event end date and time
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        if (from == null || to == null) {
            throw new IllegalArgumentException("event times must not be null");
        }
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("event end must be after event start");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the description together with the formatted event time range.
     *
     * @return the description shown in Daddy's task list
     */
    @Override
    public String getDisplayDescription() {
        return getDescription() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns the event start value for persistence.
     *
     * @return the event start date and time
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event end value for persistence.
     *
     * @return the event end date and time
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns whether another task is an event with the same description and time range.
     *
     * @param other the task to compare
     * @return whether both events represent the same scheduled work
     */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && other instanceof Event otherEvent
                && from.equals(otherEvent.from)
                && to.equals(otherEvent.to);
    }

    /**
     * Returns whether this event starts or ends on the supplied date.
     *
     * @param date the date to compare with the event range
     * @return whether either event boundary occurs on the supplied date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return from.toLocalDate().equals(date) || to.toLocalDate().equals(date);
    }
}
