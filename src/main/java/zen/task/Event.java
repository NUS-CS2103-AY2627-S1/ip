package zen.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a task that occurs between specified start and end date-times. */
public class Event extends Task {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss");
    protected LocalDateTime start;
    protected LocalDateTime end;

    /**
     * Creates an incomplete event task.
     *
     * @param description the event description.
     * @param start the event start date and time.
     * @param end the event end date and time.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Creates an incomplete event task with a priority.
     *
     * @param description the event description
     * @param start the event start date and time
     * @param end the event end date and time
     * @param priority the event priority
     */
    public Event(String description, LocalDateTime start, LocalDateTime end, Priority priority) {
        super(description, priority);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns this event in the file format used for persistence.
     *
     * @return a pipe-delimited event record
     */
    @Override
    public String toStorageString() {
        return String.format("E | %d | %s | %s to %s | %s", this.isDone ? 1 : 0,
                this.description, this.start, this.end, priority.getDisplayValue());
    }

    /**
     * Returns true if the given date falls within the event's start date
     * and end date (inclusive).
     *
     * @param date the date to compare against this event's start and end date
     * @return true if the given date falls within this event start and end date, false otherwise
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();

        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public String toString() {
        return formatWithPriority(String.format("[E][%s] %s (from: %s to: %s)", getStatusIcon(), description,
                this.start.format(FORMATTER), this.end.format(FORMATTER)));
    }
}
