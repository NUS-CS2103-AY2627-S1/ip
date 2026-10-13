package baymax.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import baymax.exception.BaymaxException;

/**
 * Represents a task that starts at one specified date and ends at another
 * specified date.
 */
public class Event extends Task {

    /** The date when the event starts. */
    private final LocalDate startDate;

    /** The date when the event ends. */
    private final LocalDate endDate;

    /**
     * Creates a new unfinished event task.
     *
     * @param description the text describing the event
     * @param startDate the date when the event starts
     * @param endDate the date when the event ends
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        assert startDate != null : "Event start dates should be parsed before construction.";
        assert endDate != null : "Event end dates should be parsed before construction.";
        TaskDate.validate(startDate);
        TaskDate.validate(endDate);
        if (!startDate.isBefore(endDate)) {
            throw new BaymaxException(" Sorry, an event must end after its start date.");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Formats the event start date for user-facing display.
     *
     * @return the start date in {@code MMM dd yyyy} format
     */
    private String formatStartDate() {
        return startDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
    }

    /**
     * Formats the event end date for user-facing display.
     *
     * @return the end date in {@code MMM dd yyyy} format
     */
    private String formatEndDate() {
        return endDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
    }

    /**
     * Returns the event type marker, task details, and event duration.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        String formattedStartDate = formatStartDate();
        String formattedEndDate = formatEndDate();
        return "[E]" + super.toString()
                + " (from: " + formattedStartDate
                + " to: " + formattedEndDate + ")";
    }

    /**
     * Returns the event fields in the format used when saving tasks.
     *
     * @return the task type, completion status, description, start date, and end date
     */
    @Override
    public String toStorageString() {
        return "E" + " | " + super.toStorageString()
                + " | " + startDate + " | " + endDate;
    }
}
