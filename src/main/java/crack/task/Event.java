package crack.task;

import java.time.LocalDate;

import crack.CrackException;

/**
 * A task that runs from one date to another.
 */
public class Event extends Task {
    private final TaskDate from;
    private final TaskDate to;

    private Event(String description, TaskDate from, TaskDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an event from dates written in any of the formats we accept.
     *
     * @param description What the event is.
     * @param from When it starts, as the user typed it.
     * @param to When it ends, as the user typed it.
     * @return The new event.
     * @throws CrackException If either date cannot be read, or the end comes first.
     */
    public static Event of(String description, String from, String to) throws CrackException {
        TaskDate start = TaskDate.parse(from);
        TaskDate end = TaskDate.parse(to);
        if (start.isAfter(end)) {
            throw new CrackException("Nah gng, an event can't wrap up before it kicks off.");
        }
        return new Event(description, start, end);
    }

    /**
     * Returns whether this event is running on the given day.
     * Both the first and last day count, so a long event matches every day it covers.
     *
     * @param date The day being asked about.
     * @return True if the event spans that day.
     */
    @Override
    public boolean isOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + from.toSaveFormat() + " | " + to.toSaveFormat();
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
