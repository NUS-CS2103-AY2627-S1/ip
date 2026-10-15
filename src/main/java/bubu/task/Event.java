package bubu.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents a task that occurs between a start and end date-time.
 */
public class Event extends Task {
    private static final String DATE_TIME_PATTERN = "MMM dd yyyy, h:mma";
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Locale.ENGLISH);
    private static final String TO_STRING_FORMAT = "[E]%s (from: %s to: %s)";
    /** Date and time at which the event starts. */
    private LocalDateTime start;
    /** Date and time at which the event ends. */
    private LocalDateTime end;

    /**
     * Creates an event task.
     *
     * @param description event description.
     * @param start event start date and time.
     * @param end event end date and time.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the event start time entered by the user.
     *
     * @return event start time.
     */
    public LocalDateTime getStart() {
        return this.start;
    }

    /**
     * Returns the event end time entered by the user.
     *
     * @return event end time.
     */
    public LocalDateTime getEnd() {
        return this.end;
    }

    @Override
    public Optional<LocalDateTime> getReminderTime() {
        return Optional.of(start);
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_FORMAT,
                super.toString(),
                this.start.format(DISPLAY_FORMAT),
                this.end.format(DISPLAY_FORMAT));
    }
}
