package jarvis.classes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Represents a task that occurs between a start and end time. */
public class Event extends Task {
    /** Deadline format for toString() to follow. */
    private static final String DATE_TIME_FORMATTER = "MM dd yyyy HH:mm";
    private static final String DATE_FORMATTER = "MM dd yyyy";
    /** Stores the event start time text. */
    private LocalDateTime startTime;
    /** Stores the event end time text. */
    private LocalDateTime endTime;

    /**
     * Creates an event task.
     *
     * @param taskName Description of the task.
     * @param startTime Event start time text.
     * @param endTime Event end time text.
     */
    public Event(String taskName, LocalDateTime startTime, LocalDateTime endTime) {
        super(taskName);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Checks if there is a schedule clash between 2 events
     *
     * @param existingEvent Existing event
     * @return boolean, indicating if the passed in event's schedule clashes with the event in question
     */
    public boolean clashesWith(Event existingEvent) {
        return startTime.isBefore(existingEvent.endTime)
                && existingEvent.startTime.isBefore(endTime);
    }

    /**
     * Formats and returns the event task in its display format.
     *
     * @return Formatted event task.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter;

        if (startTime.toLocalTime().equals(LocalTime.MIDNIGHT)
                && endTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        } else {
            formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        }

        String dateAndTime = String.format(" (from: %s to: %s)",
                startTime.format(formatter), endTime.format(formatter));
        return "[E]" + super.toString() + dateAndTime;
    }
}
