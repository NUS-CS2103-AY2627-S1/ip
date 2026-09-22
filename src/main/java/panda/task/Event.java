package panda.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import panda.exception.InvalidDateException;
import panda.exception.InvalidEventPeriodException;

/**
 * Represents a task with a stated start and end date or time.
 *
 * Both endpoints are parsed when the event is created, ensuring that every
 * stored event has valid start and end values.
 */
public class Event extends Task {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    /**
     * Creates an unfinished event task.
     *
     * @param name the event description.
     * @param startDateTimeText the supplied starting date and time.
     * @param endDateTimeText the supplied ending date and time.
     * @throws InvalidDateException if either endpoint is not a valid date and time.
     */
    public Event(String name, String startDateTimeText, String endDateTimeText)
            throws InvalidDateException {
        super(name);
        LocalDateTime parsedStartDateTime = parseDateTime(startDateTimeText);
        LocalDateTime parsedEndDateTime = parseDateTime(endDateTimeText);
        ensureValidPeriod(parsedStartDateTime, parsedEndDateTime);
        startDateTime = parsedStartDateTime;
        endDateTime = parsedEndDateTime;
    }

    /**
     * Returns the event task type.
     *
     * @return the event task type.
     */
    @Override
    public TaskType getType() {
        return TaskType.EVENT;
    }

    /**
     * Changes this event's complete time interval.
     *
     * Both values are parsed before either field is changed.
     *
     * @param updatedStartDateTimeText the replacement starting date and time.
     * @param updatedEndDateTimeText the replacement ending date and time.
     * @throws InvalidDateException if either replacement is not a valid date and time.
     */
    public void reschedule(String updatedStartDateTimeText,
            String updatedEndDateTimeText) throws InvalidDateException {
        LocalDateTime updatedStartDateTime = parseDateTime(updatedStartDateTimeText);
        LocalDateTime updatedEndDateTime = parseDateTime(updatedEndDateTimeText);
        ensureValidPeriod(updatedStartDateTime, updatedEndDateTime);
        startDateTime = updatedStartDateTime;
        endDateTime = updatedEndDateTime;
    }

    /**
     * Ensures an event does not start after it ends.
     *
     * @throws InvalidEventPeriodException if the start is after the end.
     */
    private void ensureValidPeriod(LocalDateTime proposedStartDateTime,
            LocalDateTime proposedEndDateTime) throws InvalidEventPeriodException {
        if (proposedStartDateTime.isAfter(proposedEndDateTime)) {
            throw new InvalidEventPeriodException();
        }
    }

    /**
     * Returns the event description together with its formatted time range.
     *
     * @return the formatted event description.
     */
    @Override
    public String getDisplayText() {
        return getName() + " (from: " + formatDateTimeForDisplay(startDateTime)
                + " to: " + formatDateTimeForDisplay(endDateTime) + ")";
    }

    /**
     * Checks whether this event is occurring on the supplied date.
     * Both the start and end dates are included in the event's date range.
     *
     * @param date the date used to filter the task list.
     * @return true when {@code date} falls within the event's date range.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Converts this event into one line of Panda's storage format.
     *
     * @return the common task fields followed by the start and end values.
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | "
                + escapeDataField(formatDateTimeForStorage(startDateTime))
                + " | " + escapeDataField(formatDateTimeForStorage(endDateTime));
    }
}
