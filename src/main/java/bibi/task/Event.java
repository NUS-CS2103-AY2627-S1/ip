package bibi.task;

import java.time.LocalDate;
import java.util.Optional;

import bibi.BibiException;

/**
 * Represents a task that runs from a start date to an end date, each optionally
 * carrying a time of day.
 */
public class Event extends Task {
    /** Shown when any of the three pieces of event text is missing. */
    private static final String MISSING_FIELD_MESSAGE =
            "An event needs a description, /from time, and /to time.";

    private final TaskDateTime startTime;
    private final TaskDateTime endTime;

    /**
     * Creates an event with a description, starting time, and ending time.
     *
     * @param description text describing the event
     * @param startTimeText the event start date, in one of the accepted date formats
     * @param endTimeText the event end date, in one of the accepted date formats
     * @throws BibiException if a field is blank, a date cannot be read, or the
     *     event would end before it starts
     */
    public Event(String description, String startTimeText, String endTimeText) throws BibiException {
        super(requireTaskText(description, MISSING_FIELD_MESSAGE));
        this.startTime = TaskDateTime.parse(requireTaskText(startTimeText, MISSING_FIELD_MESSAGE));
        this.endTime = TaskDateTime.parse(requireTaskText(endTimeText, MISSING_FIELD_MESSAGE));

        // Storing real dates rather than text makes this check possible at last.
        if (endTime.isBefore(startTime)) {
            throw new BibiException("An event cannot end before it starts. "
                    + "Use event <description> /from <start> /to <end>, for example: "
                    + "event study group /from 21/12/2026 1800 /to 21/12/2026 2000.");
        }
    }

    /**
     * Returns the code E, which marks an event in the list and in the save file.
     */
    @Override
    protected String getTypeCode() {
        return "E";
    }

    /**
     * Returns the start and end dates in brackets, for display after the
     * description.
     */
    @Override
    protected String getDetails() {
        return " (from: " + startTime + " to: " + endTime + ")";
    }

    /**
     * Returns the start and end dates as the two extra save-file fields.
     */
    @Override
    protected String getSaveFields() {
        return " " + FIELD_SEPARATOR + " " + startTime.toStorageString()
                + " " + FIELD_SEPARATOR + " " + endTime.toStorageString();
    }

    /**
     * Returns the moment this event starts, which is what it sorts by.
     *
     * <p>The start is used rather than the end because that is when the user
     * needs to be ready for it.
     */
    @Override
    public Optional<TaskDateTime> getScheduledTime() {
        return Optional.of(startTime);
    }

    /**
     * Reports whether this event is running on the supplied date, counting the
     * first and last days of a multi-day event as part of it.
     */
    @Override
    public boolean occursOn(LocalDate queryDate) {
        return !queryDate.isBefore(startTime.getDate()) && !queryDate.isAfter(endTime.getDate());
    }

    /**
     * Returns a confirmation noting that an event keeps its own schedule, which
     * is what separates it from a task the user can quietly postpone.
     */
    @Override
    public String describeAddition() {
        return "Added. It starts on time. Be there or be square:";
    }
}
