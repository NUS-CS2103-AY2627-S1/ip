package megia.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    /**
     * Creates an event task with its description and time range.
     *
     * @param description Description of the event.
     * @param startTime Event start date and time.
     * @param endTime Event end date and time.
     */
    public Event(String description, LocalDateTime startTime, LocalDateTime endTime) {
        super(description);
        validateTimeRange(startTime, endTime);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Restores an event task with its description, completion status, and time range.
     *
     * @param description Description of the event.
     * @param isDone Completion status of the event.
     * @param startTime Event start date and time.
     * @param endTime Event end date and time.
     */
    public Event(String description, boolean isDone, LocalDateTime startTime, LocalDateTime endTime) {
        super(description, isDone);
        validateTimeRange(startTime, endTime);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the event start date and time.
     *
     * @return Event start date and time.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Returns the event end date and time.
     *
     * @return Event end date and time.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Returns whether this event spans the specified date, including both endpoint dates.
     *
     * @param date Date to check.
     * @return True if this event spans the specified date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public String toString() {
        String endpoint = startTime.toLocalDate().equals(endTime.toLocalDate())
                ? "(on: " + startTime.format(DISPLAY_DATE_FORMATTER)
                        + ", from: " + startTime.format(DISPLAY_TIME_FORMATTER)
                        + " to: " + endTime.format(DISPLAY_TIME_FORMATTER) + ")"
                : "(from: " + startTime.format(DISPLAY_FORMATTER)
                        + " to: " + endTime.format(DISPLAY_FORMATTER) + ")";
        return "[E]" + super.toString() + " " + endpoint;
    }

    @Override
    public String encode() {
        return TaskType.EVENT.name() + "," + super.encode() + "," + startTime + "," + endTime;
    }

    private static void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        Objects.requireNonNull(startTime);
        Objects.requireNonNull(endTime);
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Event end must be after event start");
        }
    }
}
