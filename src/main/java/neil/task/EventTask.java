package neil.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Represents a task scheduled between a start and end time.
 */
public class EventTask extends Task {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event task with the specified description and schedule.
     *
     * @param description description of the task.
     * @param from start date and time in {@code yyyy-MM-dd HH:mm} format.
     * @param to end date and time in {@code yyyy-MM-dd HH:mm} format.
     * @throws java.time.format.DateTimeParseException if either date and time is invalid.
     * @throws IllegalArgumentException if the start is not earlier than the end.
     */
    public EventTask(String description, String from, String to) {
        super(description);
        this.from = LocalDateTime.parse(from, DATE_TIME_FORMATTER);
        this.to = LocalDateTime.parse(to, DATE_TIME_FORMATTER);

        if (!this.from.isBefore(this.to)) {
            throw new IllegalArgumentException("Event start must be earlier than event end");
        }
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + String.format(
                        " (from: %s to: %s)",
                        from.format(DATE_TIME_FORMATTER),
                        to.format(DATE_TIME_FORMATTER)
                );
    }

    @Override
    public String encode() {
        return String.format(
                "E | %d | %s | %s | %s",
                isDone ? 1 : 0,
                description,
                from.format(DATE_TIME_FORMATTER),
                to.format(DATE_TIME_FORMATTER)
        );
    }
}
