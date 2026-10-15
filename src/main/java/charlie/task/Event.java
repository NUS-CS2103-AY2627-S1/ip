package charlie.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import charlie.command.UpdateField;
import charlie.exception.CharlieException;

/**
 * Represents an event occurring between two specific date-times.
 */
public class Event extends Task {
    /** Date and time at which this event starts. */
    protected LocalDateTime startDateTime;

    /** Date and time at which this event ends. */
    protected LocalDateTime endDateTime;

    /**
     * Creates an event task with its description, status, start, and end.
     *
     * @param description Description of the event.
     * @param isDone Whether the event task is completed.
     * @param startDateTime Date and time at which the event starts.
     * @param endDateTime Date and time at which the event ends.
     * @throws CharlieException If the description or date-time range is invalid.
     */
    public Event(
            String description, boolean isDone, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description, isDone);
        if (startDateTime == null || endDateTime == null) {
            throw new CharlieException("Event dates cannot be empty.");
        }
        if (!startDateTime.isBefore(endDateTime)) {
            throw new CharlieException("Event end must be after its start.");
        }
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Creates a copy of this event with the requested completion status.
     *
     * @param isDone Completion status for the copy.
     * @return Copied event with the requested status.
     */
    @Override
    public Task copyWithStatus(boolean isDone) {
        return new Event(description, isDone, startDateTime, endDateTime);
    }

    /**
     * Returns whether another event has the same description, start, and end.
     *
     * @param other Task to compare with this event.
     * @return True when both events contain the same identifying details.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && startDateTime.equals(((Event) other).startDateTime)
                && endDateTime.equals(((Event) other).endDateTime);
    }

    /**
     * Returns the display representation of this event.
     *
     * @return Event type, completion status, description, start, and end.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "MMM dd yyyy, h:mm a", Locale.ENGLISH);
        return "[E]" + super.toString() + " (from: " + this.startDateTime.format(formatter)
                + " to: " + this.endDateTime.format(formatter) + ")";
    }

    /**
     * Converts this event into the format used in the save file.
     *
     * @return Serialized event data.
     */
    @Override
    public String saveFileFormat() {
        String status = isDone ? "Done" : "Not done";
        return "E" + " | " + status + " | " + this.description + " | "
                + this.startDateTime + " | " + this.endDateTime;
    }

    /**
     * Creates a copy of this event with the requested field replaced.
     *
     * @param updateField Field to replace.
     * @param newValue Replacement value for the field.
     * @return Updated copy of this event.
     * @throws CharlieException If the field is unsupported or the replacement value is invalid.
     */
    @Override
    public Task createUpdatedTask(UpdateField updateField, String newValue) {
        return switch (updateField) {
            case DESCRIPTION -> createTaskWithDescription(newValue);
            case FROM -> createTaskWithStart(newValue);
            case TO -> createTaskWithEnd(newValue);
            case DEADLINE -> throw new CharlieException(
                    "An event does not have a deadline field.");
        };
    }

    /**
     * Creates a copy of this event with a replacement description.
     *
     * @param newValue Replacement description.
     * @return Updated event copy.
     */
    private Task createTaskWithDescription(String newValue) {
        return new Event(newValue, this.isDone, this.startDateTime, this.endDateTime);
    }

    /**
     * Creates a copy of this event with a replacement start date-time.
     *
     * @param newValue Replacement start in {@code yyyy-MM-dd HHmm} format.
     * @return Updated event copy.
     * @throws CharlieException If the replacement is invalid or not before the end.
     */
    private Task createTaskWithStart(String newValue) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("uuuu-MM-dd HHmm")
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDateTime newStartDateTime = LocalDateTime.parse(newValue, dateTimeFormatter);
            if (!newStartDateTime.isBefore(endDateTime)) {
                throw new CharlieException("Event end must be after its start.");
            }

            return new Event(description, this.isDone, newStartDateTime, endDateTime);
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Event dates must use the yyyy-MM-dd HHmm format.");
        }
    }

    /**
     * Creates a copy of this event with a replacement end date-time.
     *
     * @param newValue Replacement end in {@code yyyy-MM-dd HHmm} format.
     * @return Updated event copy.
     * @throws CharlieException If the replacement is invalid or not after the start.
     */
    private Task createTaskWithEnd(String newValue) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("uuuu-MM-dd HHmm")
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDateTime newEndDateTime = LocalDateTime.parse(newValue, dateTimeFormatter);
            if (!startDateTime.isBefore(newEndDateTime)) {
                throw new CharlieException("Event end must be after its start.");
            }

            return new Event(description, this.isDone, startDateTime, newEndDateTime);
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Event dates must use the yyyy-MM-dd HHmm format.");
        }
    }
}
