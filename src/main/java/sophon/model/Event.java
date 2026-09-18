package sophon.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that occurs from one date to another.
 */
public class Event extends Task {

    /** Date when the event starts. */
    private final LocalDate fromWhen;

    /** Date when the event ends. */
    private final LocalDate toWhen;

    /**
     * Creates an event with the given description, start, and end.
     *
     * @param description details of the event.
     * @param fromWhen when the event starts.
     * @param toWhen when the event ends.
     */
    public Event(String description, LocalDate fromWhen, LocalDate toWhen) {
        super(description, TaskType.EVENT);
        assert fromWhen != null : "Event start date must not be null";
        assert toWhen != null : "Event end date must not be null";

        this.fromWhen = fromWhen;
        this.toWhen = toWhen;
    }

    /**
     * Returns this event with its dates shown in a user-friendly format.
     *
     * @return display representation of this event.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy");
        return super.toString() + " (from: " + fromWhen.format(formatter)
                + " to: " + toWhen.format(formatter) + ")";
    }

    /**
     * Returns this event in the ISO date format used by the save file.
     *
     * @return save file representation of this event.
     */
    @Override
    public String toFileString() {
        return "E | " + (isDone ? "1" : "0") + " | " + description
                + " | " + fromWhen + " | " + toWhen;
    }
}
