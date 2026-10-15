package windy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import windy.exception.InvalidInputFormatException;
import windy.storage.TaskDataFormat;
import windy.storage.TaskDataFormat.TaskType;

/**
 * Represents a task that occurs between a start and an end date or time.
 */
public class Event extends Task {

    private static final String INVALID_DATE_RANGE_MESSAGE =
            "The event end date cannot be before its start date";
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates a task that occurs over a date range.
     *
     * @param description the task description
     * @param isDone whether the task has been completed
     * @param startDate the first date of the event, in {@code yyyy-M-d} format
     * @param endDate the last date of the event, in {@code yyyy-M-d} format
     * @throws InvalidInputFormatException if either date is invalid or the end date
     *     precedes the start date
     */
    public Event(String description, boolean isDone, String startDate, String endDate)
            throws InvalidInputFormatException {
        super(description, isDone);

        LocalDate parsedStartDate;
        LocalDate parsedEndDate;
        try {
            parsedStartDate = TaskDateParser.parse(startDate);
            parsedEndDate = TaskDateParser.parse(endDate);
        } catch (DateTimeParseException exception) {
            throw new InvalidInputFormatException(TaskDateParser.INVALID_DATE_MESSAGE);
        }

        if (parsedEndDate.isBefore(parsedStartDate)) {
            throw new InvalidInputFormatException(INVALID_DATE_RANGE_MESSAGE);
        }

        this.startDate = parsedStartDate;
        this.endDate = parsedEndDate;
    }

    /** Copies a task whose dates have already been validated. */
    private Event(Event source) {
        super(source.getDescription(), source.isDone());
        this.startDate = source.startDate;
        this.endDate = source.endDate;
    }

    /** {@inheritDoc} */
    @Override
    public Task copy() {
        return new Event(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[E][" + this.getStatusIcon() + "] " + this.getDescription()
                + " (from: " + this.startDate.format(OUTPUT_FORMATTER)
                + " to: " + this.endDate.format(OUTPUT_FORMATTER) + ")";
    }

    /** {@inheritDoc} */
    @Override
    public String toDataString() {
        return TaskDataFormat.formatRecord(TaskType.EVENT, this.isDone(), this.getDescription(),
                this.startDate.toString(), this.endDate.toString());
    }

    /** {@inheritDoc} */
    @Override
    public boolean occursOnDate(LocalDate date) {
        return !date.isBefore(this.startDate) && !date.isAfter(this.endDate) && !isDone();
    }
}
