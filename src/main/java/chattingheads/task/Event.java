package chattingheads.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import chattingheads.exception.InvalidInputException;

/**
 * Represents a task that occurs between a specified start and end date and time.
 */
public class Event extends Task {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String TYPE = "E";
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    /**
     * Creates an incomplete event task with the given description,
     * start date and time, and end date and time.
     *
     * @param description   Description of the task.
     * @param startDateTime Start date and time of the event.
     * @param endDateTime   End date and time of the event.
     * @throws InvalidInputException If the end date and time is not after the start date and time.
     */
    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime)
            throws InvalidInputException {
        super(description);
        validateTimeRange(startDateTime, endDateTime);

        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Creates an event task with the given description, completion status,
     * start date and time, and end date and time.
     *
     * @param description   Description of the task.
     * @param isDone        Completion status of the task.
     * @param startDateTime Start date and time of the event.
     * @param endDateTime   End date and time of the event.
     * @throws InvalidInputException If the end date and time is not after the start date and time.
     */
    public Event(String description, boolean isDone, LocalDateTime startDateTime, LocalDateTime endDateTime)
            throws InvalidInputException {
        super(description, isDone);
        validateTimeRange(startDateTime, endDateTime);

        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    @Override
    public String toString() {
        return String.format(
                "[%s]%s (from: %s to: %s)",
                TYPE,
                super.toString(),
                DATE_TIME_FORMATTER.format(startDateTime),
                DATE_TIME_FORMATTER.format(endDateTime)
        );
    }

    @Override
    public String toCsv() {
        return String.format("%s,%s,%s,%s", TYPE, super.toCsv(), startDateTime, endDateTime);
    }

    /**
     * Changes the start and end date and time for the event.
     *
     * @param newStartDateTime New start date and time.
     * @param newEndDateTime   New end date and time.
     * @throws InvalidInputException If the end date and time is not after the start date and time.
     */
    public void reschedule(LocalDateTime newStartDateTime, LocalDateTime newEndDateTime)
            throws InvalidInputException {
        validateTimeRange(newStartDateTime, newEndDateTime);

        startDateTime = newStartDateTime;
        endDateTime = newEndDateTime;
    }

    private void validateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime)
            throws InvalidInputException {
        if (!endDateTime.isAfter(startDateTime)) {
            throw InvalidInputException.invalidTimeRange();
        }
    }
}
