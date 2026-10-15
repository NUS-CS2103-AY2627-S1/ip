package larry.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a task that occurs over a specified period.
 */
public class Event extends Task {
    private TaskDateTime startDateTime;
    private TaskDateTime endDateTime;

    /**
     * Creates an event task from start and end date-time text.
     *
     * @param description Description of the event.
     * @param startTimeText Start date and time text accepted by Larry.
     * @param endTimeText End date and time text accepted by Larry.
     * @throws IllegalArgumentException If the event does not end after it starts.
     */
    public Event(String description, String startTimeText, String endTimeText) {
        this(description, new TaskDateTime(startTimeText), new TaskDateTime(endTimeText));
    }

    /**
     * Creates an event task with parsed start and end date-time values.
     *
     * @param description Description of the event.
     * @param startDateTime Start date and time of the event.
     * @param endDateTime End date and time of the event.
     * @throws IllegalArgumentException If the event does not end after it starts.
     */
    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this(description, new TaskDateTime(startDateTime), new TaskDateTime(endDateTime));
    }

    /**
     * Creates an event task with already parsed task date and time values.
     *
     * @param description Description of the event.
     * @param startDateTime Start date and time of the event.
     * @param endDateTime End date and time of the event.
     * @throws IllegalArgumentException If the event does not end after it starts.
     */
    public Event(String description, TaskDateTime startDateTime, TaskDateTime endDateTime) {
        super(description);
        validateOrder(startDateTime, endDateTime);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Returns the event's start date and time.
     *
     * @return Start date and time of the event.
     */
    public TaskDateTime getStartDateTime() {
        return startDateTime;
    }

    /**
     * Returns the event's end date and time.
     *
     * @return End date and time of the event.
     */
    public TaskDateTime getEndDateTime() {
        return endDateTime;
    }

    /**
     * Replaces this event's start date and time without allowing an invalid duration.
     * If the saved end is legacy free-form text, ordering is deferred until both endpoints are parseable.
     *
     * @param startTimeText New start date and time text accepted by Larry.
     * @throws IllegalArgumentException If the new start is not before a parseable end.
     */
    public void updateStartDateTime(String startTimeText) {
        TaskDateTime updatedStartDateTime = new TaskDateTime(startTimeText);
        validateOrder(updatedStartDateTime, endDateTime);
        this.startDateTime = updatedStartDateTime;
    }

    /**
     * Replaces this event's end date and time without allowing an invalid duration.
     * If the saved start is legacy free-form text, ordering is deferred until both endpoints are parseable.
     *
     * @param endTimeText New end date and time text accepted by Larry.
     * @throws IllegalArgumentException If the new end is not after a parseable start.
     */
    public void updateEndDateTime(String endTimeText) {
        TaskDateTime updatedEndDateTime = new TaskDateTime(endTimeText);
        validateOrder(startDateTime, updatedEndDateTime);
        this.endDateTime = updatedEndDateTime;
    }

    /**
     * Rejects events whose parseable endpoints do not form a positive duration.
     */
    private static void validateOrder(TaskDateTime startDateTime, TaskDateTime endDateTime) {
        Optional<LocalDateTime> startValue = startDateTime.getValue();
        Optional<LocalDateTime> endValue = endDateTime.getValue();
        if (startValue.isPresent() && endValue.isPresent()
                && !endValue.get().isAfter(startValue.get())) {
            throw new IllegalArgumentException("event end must be after its start");
        }
    }

    @Override
    public boolean occursOn(LocalDate date) {
        Optional<LocalDateTime> startValue = startDateTime.getValue();
        Optional<LocalDateTime> endValue = endDateTime.getValue();
        if (startValue.isEmpty() || endValue.isEmpty()) {
            return false;
        }

        LocalDate startDate = startValue.get().toLocalDate();
        LocalDate endDate = endValue.get().toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Returns the task in the format used by Larry's responses.
     *
     * @return Status icon followed by the task description.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + startDateTime
                + " to: " + endDateTime + ")";
    }
}
