package megatron.task;

import java.time.LocalDateTime;

/**
 * A task with a specified start and end date or time.
 */
public class Event extends Task {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    /**
     * Creates an event by parsing supported user start and end date/time values.
     *
     * @param description The event description.
     * @param startInput The supported event start input.
     * @param endInput The supported event end input.
     */
    public Event(String description, String startInput, String endInput) {
        super(description, TaskType.EVENT);
        startDateTime = DatetimeValidator.parseToLocalDateTime(startInput);
        LocalDateTime parsedEndDateTime;
        try {
            parsedEndDateTime = DatetimeValidator.parseToLocalDateTime(endInput);
        } catch (IllegalArgumentException exception) {
            // A time-only end value is interpreted on the start date.
            parsedEndDateTime = DatetimeValidator.parseToLocalDateTime(
                    startDateTime.toLocalDate() + " " + endInput);
        }
        if (!parsedEndDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("An event must end after its start.");
        }
        endDateTime = parsedEndDateTime;
    }

    /**
     * Recreates an event from its ISO local date/time storage values.
     *
     * @param description The event description.
     * @param startDateTime The stored event start value.
     * @param endDateTime The stored event end value.
     */
    public Event(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description, TaskType.EVENT);
        if (!endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("An event must end after its start.");
        }
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtra() {
        return startDateTime + "|" + endDateTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + DatetimeValidator.formatForUser(startDateTime)
                + " to: " + DatetimeValidator.formatForUser(endDateTime) + ")";
    }
}
