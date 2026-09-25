package goat;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a task that occurs between specified start and end dates.
 */
public class Event extends Task {
    /** First date of this event. */
    protected LocalDate startDate;
    /** Last date of this event. */
    protected LocalDate endDate;

    /**
     * Creates an incomplete event with the given description and date range.
     *
     * @param description description of the event
     * @param from date on which the event starts
     * @param to date on which the event ends
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.startDate = from;
        this.endDate = to;
    }

    /**
     * Returns this event in the data-file format.
     *
     * @return serialized event data
     */
    @Override
    public String toFileString() {
        return formatFileData("E", startDate.toString(), endDate.toString());
    }

    /**
     * Uses the start date when sorting events chronologically.
     *
     * @return event start date
     */
    @Override
    public Optional<LocalDate> getSchedulingDate() {
        return Optional.of(startDate);
    }

    /**
     * Returns the event with its type, completion status, and date range.
     *
     * @return formatted event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + TaskDate.format(startDate)
                + " to: " + TaskDate.format(endDate) + ")";
    }
}
