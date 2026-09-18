package meowmeow.task;

import meowmeow.MeowMeowException;

import java.time.LocalDate;

/**
 * Represents a task that spans a start and an end date or time.
 */
public class Event extends Task {

    /** Date or time the event starts */
    protected TaskDateTime from;
    /** Date or time the event ends */
    protected TaskDateTime to;

    /**
     * Constructs an event with the given description, start and end times.
     *
     * @param description Description of the event.
     * @param from Date or time the event starts.
     * @param to Date or time the event ends.
     * @throws MeowMeowException If the start is not strictly before the end.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) throws MeowMeowException{
        super(description);
        if (to.isBefore(from)) {
            throw new MeowMeowException("An event has to start before it ends. "
                    + "You gave " + from + " to " + to + ".");
        }
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from.toStorageFormat() + " | " + to.toStorageFormat();
    }

    @Override
    public boolean isDuplicateOf(Task other) {
        if (!super.isDuplicateOf(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }
}
