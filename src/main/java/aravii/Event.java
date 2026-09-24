package aravii;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task with a start and an end date and time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy, HH:mm", Locale.ENGLISH);

    private final LocalDateTime start;

    private final LocalDateTime end;

    /**
     * Creates an event whose end is not before its start.
     *
     * @param description The task description.
     * @param start The start date and time.
     * @param end The end date and time.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        if (start == null || end == null) {
            throw new IllegalArgumentException("Event start and end must not be null.");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("An event must not end before it starts.");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the stored start date and time.
     *
     * @return The start date and time.
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Returns the stored end date and time.
     *
     * @return The end date and time.
     */
    public LocalDateTime getEnd() {
        return end;
    }

    @Override
    TaskType getType() {
        return TaskType.EVENT;
    }

    @Override
    String formatDetails() {
        return "(from: " + start.format(DISPLAY_FORMAT) + " to: " + end.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    String formatStorageDetails() {
        return "(from: " + start.format(Parser.DATE_TIME_FORMAT)
                + " to: " + end.format(Parser.DATE_TIME_FORMAT) + ")";
    }
}
