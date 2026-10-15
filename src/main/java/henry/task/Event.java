package henry.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that takes place between specified start and end times.
 */
public class Event extends Task {
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d uuuu h:mm a", Locale.US);
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    /**
     * Creates an event task that is initially not done.
     *
     * @param description description of the task.
     * @param startTime date and time at which the event starts.
     * @param endTime date and time at which the event ends.
     * @throws IllegalArgumentException if the event does not end after it starts.
     */
    public Event(String description, LocalDateTime startTime, LocalDateTime endTime) {
        super(description);
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Event end must be after its start");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + startTime + " | " + endTime;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + startTime.format(OUTPUT_FORMATTER)
                + " to: " + endTime.format(OUTPUT_FORMATTER) + ")";
    }
}
