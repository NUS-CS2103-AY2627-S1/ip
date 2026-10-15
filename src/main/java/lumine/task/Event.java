package lumine.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lumine.LumineException;

/**
 * Represents a task that spans a time range, with an explicit start ({@code /from}) and
 * end ({@code /to}) time.
 *
 * <p>Both time fields must be free text or date-times with an end strictly after the start.
 * Date-times use {@code yyyy MM dd HHmm}.</p>
 */
public class Event extends Task {
    private static final String DATE_FORMAT_ERROR = "Sorry, you need to enter date in format yyyy MM dd HHmm "
            + "for both from and to date.";
    private static final String DATE_RANGE_ERROR = "Hmmmm, the event end date and start date isn't valid, "
            + "try again with a valid range instead";
    private final TaskDateTime startTime;
    private final TaskDateTime endTime;

    /**
     * Creates a new event task.
     *
     * @param description The task description (must not be blank).
     * @param from        The start time string (must not be blank).
     * @param to          The end time string (must not be blank).
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.startTime = new TaskDateTime(normalizeEventTime(requireText(from, "event start time")));
        this.endTime = new TaskDateTime(normalizeEventTime(requireText(to, "event end time")));
        validateDateRange();
    }

    /**
     * Rejects date arguments without a four-digit time, preserving free-form text.
     */
    private String normalizeEventTime(String text) {
        String normalizedText = text.trim().replaceAll("\\s+", " ");
        boolean hasDateTimeFormat = TaskDateTime.hasDateTimeFormat(normalizedText);
        if (TaskDateTime.hasDatePrefix(normalizedText) && !hasDateTimeFormat) {
            throw new LumineException(DATE_FORMAT_ERROR);
        }
        return hasDateTimeFormat ? normalizedText : text;
    }

    /**
     * Requires paired date-times and compares their full dates and times.
     */
    private void validateDateRange() {
        LocalDateTime startDateTime = startTime.toLocalDateTime();
        LocalDateTime endDateTime = endTime.toLocalDateTime();
        if ((startDateTime == null) != (endDateTime == null)) {
            throw new LumineException(DATE_FORMAT_ERROR);
        }
        if (startDateTime != null && !endDateTime.isAfter(startDateTime)) {
            throw new LumineException(DATE_RANGE_ERROR);
        }
    }

    /**
     * {@inheritDoc}
     * Returns the pipe-delimited storage representation, including the
     * {@code from} and {@code to} fields.
     */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + escapeStorageField(startTime.formatForStorage())
                + " | " + escapeStorageField(endTime.formatForStorage());
    }

    /**
     * {@inheritDoc}
     * Returns the human-readable representation, appending {@code (from: ... to: ...)}.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + startTime.formatForDisplay()
                + " to: " + endTime.formatForDisplay() + ")";
    }

    /**
     * Returns the calendar date on which this event ends, or null when it is plain text.
     */
    public LocalDate getToDate() {
        return endTime.toLocalDate();
    }

    /**
     * {@inheritDoc}
     * Uses the event end date for the comparison.
     */
    @Override
    public boolean isDueOn(LocalDate date) {
        return date != null && date.equals(endTime.toLocalDate());
    }
}
