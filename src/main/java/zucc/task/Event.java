package zucc.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import zucc.ZuccException;

/**
 * Represents a task that takes place between a start and an end date or time.
 */
public class Event extends Task {
    /** Short identifier used when displaying and storing events. */
    static final String TYPE_CODE = "E";

    /** Error used when an event is missing any of its required values. */
    private static final String INVALID_EVENT_ERROR =
            "Zucc needs more data: add an event description "
                    + "followed by /from and /to times.";

    /** Error used when an event does not end after it starts. */
    private static final String INVALID_EVENT_RANGE_ERROR =
            "Zucc can't schedule an event unless its end is after its start.";

    /** Date and time at which the event starts. */
    private final LocalDateTime startDateTime;

    /** Date and time at which the event ends. */
    private final LocalDateTime endDateTime;

    /**
     * Creates an incomplete event with the given description and time range.
     *
     * @param description description of the event.
     * @param startDateTimeText start date and time in {@code d/M/yyyy HHmm} format.
     * @param endDateTimeText end date and time in {@code d/M/yyyy HHmm} format.
     * @throws ZuccException if a required value or date range is invalid.
     */
    public Event(String description, String startDateTimeText, String endDateTimeText)
            throws ZuccException {
        this(description, startDateTimeText, endDateTimeText, Priority.NONE);
    }

    /**
     * Creates an incomplete event with the given description, time range, and priority.
     *
     * @param description description of the event.
     * @param startDateTimeText start date and time in {@code d/M/yyyy HHmm} format.
     * @param endDateTimeText end date and time in {@code d/M/yyyy HHmm} format.
     * @param priority importance assigned to the task.
     * @throws ZuccException if a supplied value or the date range is invalid.
     */
    public Event(String description, String startDateTimeText, String endDateTimeText,
            Priority priority) throws ZuccException {
        super(requireNonBlank(description, INVALID_EVENT_ERROR), priority);
        this.startDateTime = TaskDateTimeFormat.parse(
                requireNonBlank(startDateTimeText, INVALID_EVENT_ERROR));
        this.endDateTime = TaskDateTimeFormat.parse(
                requireNonBlank(endDateTimeText, INVALID_EVENT_ERROR));
        requireValidRange();
    }

    /**
     * Reconstructs an event from decoded storage fields.
     * The field-count check runs before the superclass constructor so indexing is safe.
     *
     * @param fields decoded type, status, priority, description, start, and end.
     * @throws ZuccException if the fields do not describe a valid event.
     */
    Event(String[] fields) throws ZuccException {
        if (fields == null || fields.length != 6) {
            throw new ZuccException("Invalid stored event.");
        }
        super(fields[3], fields[1], fields[2]);
        this.startDateTime = TaskDateTimeFormat.parse(
                requireNonBlank(fields[4], INVALID_EVENT_ERROR));
        this.endDateTime = TaskDateTimeFormat.parse(
                requireNonBlank(fields[5], INVALID_EVENT_ERROR));
        requireValidRange();
    }

    /**
     * Ensures that this event has a positive duration.
     *
     * @throws ZuccException if the event has a zero or negative duration.
     */
    private void requireValidRange() throws ZuccException {
        if (!endDateTime.isAfter(startDateTime)) {
            throw new ZuccException(INVALID_EVENT_RANGE_ERROR);
        }
    }

    /**
     * Reports whether any part of this event occurs on a given date.
     * Both the start and end dates are included.
     *
     * @param date date to check.
     * @return {@code true} if the event occurs on the date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(startDateTime.toLocalDate())
                && !date.isAfter(endDateTime.toLocalDate());
    }

    /**
     * Supplies the plain subtype fields needed to store this event.
     *
     * @return type code followed by the start and end times.
     */
    @Override
    protected String[] getStorageFields() {
        return new String[] {
            TYPE_CODE,
            TaskDateTimeFormat.formatForStorage(startDateTime),
            TaskDateTimeFormat.formatForStorage(endDateTime)
        };
    }

    /**
     * Formats this task with its type marker and time range.
     *
     * @return the task in {@code [E][status] description (from: start to: end)} format.
     */
    @Override
    public String toString() {
        return "[" + TYPE_CODE + "]" + super.toString()
                + " (from: " + TaskDateTimeFormat.formatForDisplay(startDateTime)
                + " to: " + TaskDateTimeFormat.formatForDisplay(endDateTime) + ")";
    }
}
