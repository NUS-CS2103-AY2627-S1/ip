package bot.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents a task that occurs between a start and end date or time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);

    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    /**
     * Creates an event task.
     *
     * @param description description of the event.
     * @param startDateTime date and time at which the event starts.
     * @param endDateTime date and time at which the event ends.
     */
    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description);
        assert startDateTime != null : "Event start date and time must not be null";
        assert endDateTime != null : "Event end date and time must not be null";
        assert endDateTime.isAfter(startDateTime) : "Event end must be after its start";

        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Returns the event in the format used by the storage file.
     *
     * @return storage representation containing the task type, start, and end times
     */
    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + startDateTime + " | " + endDateTime;
    }

    /**
     * Checks whether this event is in progress on the specified date.
     * Both the start and end dates are treated as part of the event.
     *
     * @param date date to check.
     * @return {@code true} if the date is within the event's date range, inclusive
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Returns the event start date and time as its chronological sort key.
     *
     * @return the event start date and time
     */
    @Override
    public Optional<LocalDateTime> getChronologicalSortKey() {
        return Optional.of(startDateTime);
    }

    /**
     * Checks whether another event has the same description and time range.
     *
     * @param other task to compare.
     * @return {@code true} if both events have the same identifying details
     */
    @Override
    boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && startDateTime.equals(((Event) other).startDateTime)
                && endDateTime.equals(((Event) other).endDateTime);
    }

    /**
     * Returns the event's type, status, description, and formatted time range for display.
     *
     * @return display representation of the event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + startDateTime.format(DISPLAY_FORMAT)
                + " to: " + endDateTime.format(DISPLAY_FORMAT) + ")";
    }
}
