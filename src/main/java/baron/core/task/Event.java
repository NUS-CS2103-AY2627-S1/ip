package baron.core.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a task that takes place between a start and end time.
 */
public class Event extends Task {
    private final LocalDateTime fromDate;
    private final LocalDateTime toDate;

    /**
     * Creates an event task with the specified description and time range.
     *
     * @param description The task description.
     * @param fromDate The event start time.
     * @param toDate The event end time.
     */
    public Event(String description, LocalDateTime fromDate, LocalDateTime toDate) {
        this(UUID.randomUUID(), description, fromDate, toDate);
    }

    /**
     * Creates an event task with the specified identifier, description, and time range.
     *
     * @param uuid The task identifier.
     * @param description The task description.
     * @param fromDate The event start time.
     * @param toDate The event end time.
     */
    public Event(UUID uuid, String description, LocalDateTime fromDate, LocalDateTime toDate) {
        super(uuid, TaskType.EVENT, description);
        assert fromDate != null && toDate != null : "Event tasks must have start and end times";
        assert fromDate.isBefore(toDate) : "An event's start time must be before its end time";
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * Returns this event task in the persistent storage format.
     *
     * @return The persistent event-task representation.
     */
    @Override
    public String toFileString() {
        return String.join(" | ", super.toFileString(), fromDate.toString(), toDate.toString());
    }

    /**
     * Returns a user-facing representation of this event task.
     *
     * @return The formatted event task.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a, d MMM yyyy");
        return super.toString()
                + String.format("\nfrom: %s", fromDate.format(formatter))
                + String.format("\nto: %s", toDate.format(formatter));
    }
}
