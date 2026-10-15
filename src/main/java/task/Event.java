package task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import exception.CbtException;

/**
 * Represents a task with a start and end date and time.
 */
public class Event extends Task {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    /**
     * Creates an incomplete event.
     *
     * @param description description of the event.
     * @param startDate start date and time.
     * @param endDate end date and time.
     * @throws CbtException if the event does not start before it ends.
     */
    public Event(String description, LocalDateTime startDate, LocalDateTime endDate)
            throws CbtException {
        super(description);
        assert startDate != null : "Event start date and time must not be null";
        assert endDate != null : "Event end date and time must not be null";
        if (!startDate.isBefore(endDate)) {
            throw new CbtException("The event start date and time must be before its end date and time.");
        }
        assert startDate.isBefore(endDate) : "Validated event range must be chronological";
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns this event in its user-facing format.
     *
     * @return formatted event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + startDate.format(DATE_TIME_PRINT_FORMATTER)
                + " to: " + endDate.format(DATE_TIME_PRINT_FORMATTER) + ")";
    }

    /**
     * Serializes this event for persistent storage.
     *
     * @return event in the application's file format.
     */
    @Override
    public String toFileFormat() {
        return "EVENT | " + (isDone() ? "1" : "0") + " | " + getDescription() + " | "
                + startDate.format(DATE_TIME_WRITE_FORMATTER) + " | "
                + endDate.format(DATE_TIME_WRITE_FORMATTER);
    }

    /**
     * Checks whether this event spans the specified date, including its endpoints.
     *
     * @param date date to check.
     * @return {@code true} if the event occurs at any time on the specified date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(startDate.toLocalDate()) && !date.isAfter(endDate.toLocalDate());
    }

    @Override
    public boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return startDate.equals(otherEvent.startDate) && endDate.equals(otherEvent.endDate);
    }

    @Override
    public Optional<LocalDateTime> getChronologicalDateTime() {
        return Optional.of(startDate);
    }
}
