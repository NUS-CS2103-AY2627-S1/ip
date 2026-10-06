package duke.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import duke.exception.DukeException;

/**
 * Represents an event task with a start and end time.
 */
public class Event extends Task {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a");

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    /**
     * Constructs an Event task with the given description, start time, and end time.
     *
     * @param name      The description of the event task.
     * @param startTime The start date/time string (yyyy-MM-dd or yyyy-MM-dd HHmm).
     * @param endTime   The end date/time string (yyyy-MM-dd or yyyy-MM-dd HHmm).
     * @throws DukeException If any date/time string format is invalid or start time is after end time.
     */
    public Event(String name, String startTime, String endTime) throws DukeException {
        super(name);
        this.startDate = parseDateTime(startTime);
        this.endDate = parseDateTime(endTime);
        if (this.startDate.isAfter(this.endDate)) {
            throw new DukeException("OOPS!!! The event start time cannot be after the end time.");
        }
    }

    /**
     * Constructs an Event task with the given description, dates, and status.
     *
     * @param name      The description of the event task.
     * @param startDate The start date/time.
     * @param endDate   The end date/time.
     * @param isDone    Whether the task is completed.
     * @throws DukeException If the start time is after the end time.
     */
    Event(String name, LocalDateTime startDate, LocalDateTime endDate, boolean isDone) throws DukeException {
        super(name);
        if (startDate.isAfter(endDate)) {
            throw new DukeException("OOPS!!! The event start time cannot be after the end time.");
        }
        this.startDate = startDate;
        this.endDate = endDate;
        if (isDone) {
            markAsDone();
        }
    }

    /**
     * Parses the date string into a LocalDateTime object.
     *
     * @param input The raw date string.
     * @return The parsed LocalDateTime.
     * @throws DukeException If the date cannot be parsed.
     */
    private LocalDateTime parseDateTime(String input) throws DukeException {
        try {
            if (input.contains("T")) {
                return LocalDateTime.parse(input);
            } else if (input.contains(" ")) {
                return LocalDateTime.parse(input, INPUT_FORMAT);
            } else {
                return LocalDate.parse(input).atStartOfDay();
            }
        } catch (DateTimeParseException e) {
            throw new DukeException(
                    "Invalid date format. Please use yyyy-MM-dd or yyyy-MM-dd HHmm (e.g., 2019-10-15 1800).");
        }
    }

    /**
     * Formats the Event for saving to a file.
     *
     * @return A pipe-separated string representing the Event's state.
     */
    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + startDate + " | " + endDate;
    }

    /**
     * Returns the task type icon for an event.
     *
     * @return The string "E".
     */
    @Override
    public String getTaskIcon() {
        return "E";
    }

    /**
     * Returns a string representation of the event task.
     *
     * @return The formatted event task string.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + startDate.format(OUTPUT_FORMAT)
                + " to: " + endDate.format(OUTPUT_FORMAT) + ")";
    }

    /**
     * Creates and returns a copy of this Event task.
     *
     * @return A copy of this Event task.
     */
    @Override
    public Event copy() {
        try {
            return new Event(getName(), this.startDate, this.endDate, isDone());
        } catch (DukeException e) {
            throw new AssertionError("Validated dates in Event should not fail copy", e);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Event otherEvent = (Event) other;
        return getName().equals(otherEvent.getName())
                && startDate.equals(otherEvent.startDate)
                && endDate.equals(otherEvent.endDate);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getName(), startDate, endDate);
    }
}
