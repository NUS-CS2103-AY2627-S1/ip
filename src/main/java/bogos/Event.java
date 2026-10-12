package bogos;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents an event task.
 */
public class Event extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an incomplete event with the given description and date range.
     *
     * @param description Description of the event.
     * @param startDate Date on which the event starts.
     * @param endDate Date on which the event ends.
     * @throws IllegalArgumentException If the description is blank or the end is before the start.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        this(description, startDate, endDate, List.of());
    }

    /**
     * Creates an incomplete event with the given description, dates, and tags.
     *
     * @param description Description of the event.
     * @param startDate Date on which the event starts.
     * @param endDate Date on which the event ends.
     * @param tags Tags assigned to the event.
     * @throws IllegalArgumentException If the description, dates, or tags are invalid.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate, List<String> tags) {
        super(TaskType.EVENT, description, tags);
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Event end date must not be before its start date.");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the start date in the format used by the console UI.
     *
     * @return Formatted start date.
     */
    public String getFormattedStartDate() {
        return startDate.format(DISPLAY_DATE_FORMATTER);
    }

    /**
     * Returns the end date in the format used by the console UI.
     *
     * @return Formatted end date.
     */
    public String getFormattedEndDate() {
        return endDate.format(DISPLAY_DATE_FORMATTER);
    }

    /**
     * Returns this event in its user-facing display format.
     *
     * @return User-facing event description.
     */
    @Override
    public String toString() {
        return appendTags(getBasicDisplayFormat()
                + " (from: " + getFormattedStartDate()
                + " to: " + getFormattedEndDate() + ")");
    }

    /**
     * Returns this event in the format used by the data file.
     *
     * @return Data-file representation of this event.
     */
    @Override
    public String toFileFormat() {
        return appendTags(getBasicFileFormat()
                + " | " + getStartDate()
                + " | " + getEndDate());
    }
}
