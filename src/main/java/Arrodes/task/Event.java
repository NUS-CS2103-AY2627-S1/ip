package arrodes.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** A task that takes place between two date-time values. */
public class Event extends Task {

    /** Starting date and time of the event. */
    private final LocalDateTime startAt;
    /** Ending date and time of the event. */
    private final LocalDateTime endAt;
    /** Whether the input explicitly included a time for the start endpoint. */
    private final boolean isStartTimeIncluded;
    /** Whether the input explicitly included a time for the end endpoint. */
    private final boolean isEndTimeIncluded;

    /**
     * Creates a timed event whose endpoints are displayed with times.
     * @param description text describing the event
     * @param startAt event start
     * @param endAt event end
     * @throws IllegalArgumentException if an endpoint is null or the end is not after the start
     */
    public Event(String description, LocalDateTime startAt, LocalDateTime endAt) {
        this(description, startAt, endAt, true, true);
    }

    /**
     * Creates an event while preserving whether either input endpoint included a time.
     * @param description text describing the event
     * @param startAt event start
     * @param endAt event end
     * @param isStartTimeIncluded whether the input included a start time
     * @param isEndTimeIncluded whether the input included an end time
     * @throws IllegalArgumentException if an endpoint is null or the end is not after the start
     */
    public Event(String description, LocalDateTime startAt, LocalDateTime endAt,
                 boolean isStartTimeIncluded, boolean isEndTimeIncluded) {
        super(description);
        if (startAt == null || endAt == null || !endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("Event times are invalid.");
        }
        this.startAt = startAt;
        this.endAt = endAt;
        this.isStartTimeIncluded = isStartTimeIncluded;
        this.isEndTimeIncluded = isEndTimeIncluded;
    }

    /**
     * Returns the event's starting-time text.
     *
     * @return starting date and time
     */
    public LocalDateTime getStartAt() {
        return startAt;
    }

    /**
     * Returns the event's ending-time text.
     *
     * @return ending date and time
     */
    public LocalDateTime getEndAt() {
        return endAt;
    }

    /**
     * Returns the event with a type prefix and formatted endpoints.
     * @return formatted event text
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to %s)", super.toString(),
                formatEndpoint(startAt, isStartTimeIncluded), formatEndpoint(endAt, isEndTimeIncluded));
    }

    /**
     * Formats an endpoint according to whether its input included a time.
     * @param endpoint endpoint to format
     * @param isTimeIncluded whether to include hours and minutes
     * @return formatted endpoint
     */
    private String formatEndpoint(LocalDateTime endpoint, boolean isTimeIncluded) {
        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern(
                isTimeIncluded ? "MMM dd yyyy HH:mm" : "MMM dd yyyy", Locale.ENGLISH);
        return endpoint.format(displayFormat);
    }
}
