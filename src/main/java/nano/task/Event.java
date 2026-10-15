package nano.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import nano.DateTimeUtil;
import nano.NanoException;

/**
 * Represents a task that takes place between a specified start and end time.
 */
public class Event extends Task {
    private LocalDateTime from;
    private LocalDateTime to;

    /**
     * Creates an event task with the specified description, start time, and end time.
     *
     * @param description description of the event.
     * @param from start date or time of the event.
     * @param to end date or time of the event.
     * @throws NanoException if the description, start time, or end time is empty.
     */
    public Event(String description, String from, String to) throws NanoException {
        super(description);

        validateTimesArePresent(from, to);

        this.from = parseDateTime(from);
        this.to = parseDateTime(to);

        validateTimeOrder();
    }

    /**
     * Ensures that both event times are provided.
     *
     * @param from event start time.
     * @param to event end time.
     * @throws NanoException if either time is empty.
     */
    private void validateTimesArePresent(String from, String to)
            throws NanoException {
        if (from.trim().isEmpty()) {
            throw new NanoException(
                    "The /from time of an event cannot be empty."
            );
        }

        if (to.trim().isEmpty()) {
            throw new NanoException(
                    "The /to time of an event cannot be empty."
            );
        }
    }

    /**
     * Parses an event date and time.
     *
     * @param dateTimeText date and time in yyyy-MM-dd HHmm format.
     * @return the parsed date and time.
     * @throws NanoException if the value is not a valid date and time.
     */
    private LocalDateTime parseDateTime(String dateTimeText)
            throws NanoException {
        try {
            return LocalDateTime.parse(
                    dateTimeText.trim(),
                    DateTimeUtil.STORAGE_FORMATTER
            );
        } catch (DateTimeParseException e) {
            throw new NanoException(
                    "Event dates must use yyyy-MM-dd HHmm format."
            );
        }
    }

    /**
     * Ensures that the event ends after it starts.
     *
     * @throws NanoException if the end time is before or equal to the start time.
     */
    private void validateTimeOrder() throws NanoException {
        if (!to.isAfter(from)) {
            throw new NanoException(
                    "The event must end after it starts."
            );
        }
    }

    @Override
    public String toStorageString() {
        return "E | " + (isDone() ? "1" : "0")
                + " | " + getPriorityText()
                + " | " + getDescription()
                + " | " + DateTimeUtil.formatForStorage(from)
                + " | " + DateTimeUtil.formatForStorage(to);
    }

    @Override
    public String toString() {
        return "[E]" + getStatusIcon() + " " + getDescription()
                + " (from: " + DateTimeUtil.formatForDisplay(from)
                + " to: " + DateTimeUtil.formatForDisplay(to) + ")"
                + " (priority: " + getPriorityText() + ")";
    }
}
