package stephen.task;

import java.time.LocalDate;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an incomplete event.
     *
     * @param description description of the event
     * @param from start date
     * @param to end date
     * @throws IllegalArgumentException if a date is null or the end date precedes the start date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(TaskType.EVENT, description);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Event dates must not be null");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Event end date cannot be before its start date");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Checks whether this event includes the given date.
     *
     * @param date date to check
     * @return {@code true} if the date is within the inclusive event range
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    /**
     * Returns this event in the save-file format.
     *
     * @return serialized task data followed by the start and end dates
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + from.format(INPUT_DATE_FORMAT)
                + " | " + to.format(INPUT_DATE_FORMAT);
    }

    /**
     * Returns this event in the chatbot display format.
     *
     * @return task details followed by the formatted date range
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
