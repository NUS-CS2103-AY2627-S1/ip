package grower.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that occurs between specified start and end times.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a");

    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Creates an event with a start and end date and time.
     *
     * @param description Description of the event.
     * @param start Date and time at which the event begins.
     * @param end Date and time at which the event ends.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toFileString() {
        return String.format(
                "E | %d | %s | %s | %s",
                isCompleted() ? 1 : 0,
                getDescription(),
                start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    @Override
    public LocalDateTime getSortTime() {
        return end;
    }

    /**
     * Returns a display string containing the task and its start and end times.
     *
     * @return Display representation of this event task.
     */
    @Override
    public String toString() {
        return String.format(
                "[E]%s (from: %s to: %s)",
                super.toString(),
                start.format(DISPLAY_FORMATTER),
                end.format(DISPLAY_FORMATTER)
        );
    }
}
