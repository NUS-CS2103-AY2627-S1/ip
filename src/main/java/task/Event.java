package puyo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import puyo.PuyoException;
import puyo.parser.Parser;

/**
 * Represents an event task that occurs within a specific start and end time.
 */
public class Event extends Task {

    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Constructs an {@code Event} task with the specified description, start time, and end time.
     * Enforces validation to ensure the start time is strictly before the end time.
     *
     * @param description The detailed description of the event.
     * @param start The starting date and time of the event.
     * @param end The ending date and time of the event.
     * @throws PuyoException If the start time is after or equal to the end time.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) throws PuyoException {
        super(description, TaskType.EVENT); // Passes both required arguments to the parent Task constructor

        // Validate that the start time is strictly before the end time
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new PuyoException("Oops! Start time cannot be after or equal to end time.");
        }

        this.start = start;
        this.end = end;
    }

    /**
     * Returns whether the date is between the event's start and end dates, inclusive.
     * This date-based rule includes the end date even when the event ends at midnight.
     * Implemented with assistance from ChatGPT.
     *
     * @param date The date to check.
     * @return {@code true} for the start date, end date, and every date in between.
     */
    @Override
    public boolean isScheduledOn(LocalDate date) {
        return !date.isBefore(start.toLocalDate()) && !date.isAfter(end.toLocalDate());
    }

    /**
     * Formats the event task for storage file persistence.
     *
     * @return Formatted string representation for file storage.
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + start.format(Parser.SAVE_DATETIME) + " | "
                + end.format(Parser.SAVE_DATETIME);
    }

    /**
     * Formats the event task for user interface display.
     *
     * @return Formatted string representation of the event task.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + start.format(Parser.DISPLAY_DATETIME) + " to: "
                + end.format(Parser.DISPLAY_DATETIME) + ")";
    }
}
