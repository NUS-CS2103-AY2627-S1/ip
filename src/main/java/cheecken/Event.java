package cheecken;

import java.time.Clock;

/**
 * Represents a task with a start and end date or date-time.
 */
public class Event extends Task {
    private final DateTimeValue startTime;
    private final DateTimeValue endTime;

    /**
     * Creates an event using one local clock reading for both endpoints.
     * @param task description of the task
     * @param startTime event start date or date-time
     * @param endTime event end date or date-time
     */
    public Event(String task, String startTime, String endTime) {
        this(task, startTime, endTime, Clock.systemDefaultZone());
    }

    /**
     * Resolves both event endpoints independently against the supplied clock.
     * @param task description of the task
     * @param startTime event start date or date-time
     * @param endTime event end date or date-time
     * @param clock clock used to resolve relative dates and times
     */
    Event(String task, String startTime, String endTime, Clock clock) {
        super(task);
        Clock reference = Clock.fixed(clock.instant(), clock.getZone());
        this.startTime = DateTimeValue.parse(startTime, "event", reference);
        this.endTime = DateTimeValue.parse(endTime, "event", reference);
    }

    /**
     * Restores already resolved endpoints without reinterpreting saved dates.
     * @param task description of the task
     * @param startTime event start date or date-time
     * @param endTime event end date or date-time
     */
    Event(String task, DateTimeValue startTime, DateTimeValue endTime) {
        super(task);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the event display representation.
     * @return formatted task description and completion state
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + startTime.display() + " to: " + endTime.display() + ")";
    }

    /**
     * Serializes the resolved endpoints, preserving whether each time was supplied.
     * @return task record formatted for persistence
     */
    @Override
    public String toStorageString() {
        return "E | " + (isMarked ? "1" : "0") + " | " + task + " | "
                + startTime.storage() + " | " + endTime.storage();
    }
}
