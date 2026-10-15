package rocky.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Represents a task that occurs between a start and end time.
 */
public class Event extends Task {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final boolean hasScheduledTimes;

    /**
     * Creates an event task with a description, start-time text, and end-time text.
     *
     * @param description the text describing the event
     * @param startDate the date on which the event starts
     * @param endDate the date on which the event ends
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        this.startDateTime = startDate.atStartOfDay();
        this.endDateTime = endDate.atStartOfDay();
        validateDateRange(startDateTime, endDateTime);
        this.hasScheduledTimes = false;
    }

    /**
     * Creates an event with start and end dates and times.
     *
     * @param description the text describing the event
     * @param startDateTime the date and time at which the event starts
     * @param endDateTime the date and time at which the event ends
     */
    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        validateDateRange(startDateTime, endDateTime);
        this.hasScheduledTimes = true;
    }

    /** Rejects an event whose end precedes its start. */
    private static void validateDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (endDateTime.isBefore(startDateTime)) {
            throw new IllegalArgumentException("Event end date/time cannot be earlier than its start date/time.");
        }
    }

    /** Returns the event start used for chronological sorting.
     *
     * @return this event's start date and time.
     */
    @Override
    public Optional<LocalDateTime> getSortDate() {
        return Optional.of(startDateTime);
    }

    /**
     * Returns this event task in the chatbot's display format.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        DateTimeFormatter format = hasScheduledTimes ? DISPLAY_DATE_TIME_FORMAT : DISPLAY_DATE_FORMAT;
        return "[E][" + getStatusIcon() + "] " + getDescription()
                + " (from: " + startDateTime.format(format)
                + " to: " + endDateTime.format(format) + ")";
    }

    /**
     * Returns the type, status, description, and times in the task file format.
     *
     * @return the storage representation of this event
     */
    @Override
    public String toStorageString() {
        String storedFrom = hasScheduledTimes
                ? startDateTime.toString()
                : startDateTime.toLocalDate().toString();
        String storedTo = hasScheduledTimes
                ? endDateTime.toString()
                : endDateTime.toLocalDate().toString();
        return "E | " + getStatusValue() + " | " + getDescription()
                + " | " + storedFrom + " | " + storedTo;
    }
}
