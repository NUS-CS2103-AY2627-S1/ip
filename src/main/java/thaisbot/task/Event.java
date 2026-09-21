package thaisbot.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A task representing an event spanning a start and end time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy HHmm");

    private final LocalDateTime from;
    private final boolean hasFromTime;
    private final LocalDateTime to;
    private final boolean hasToTime;

    /**
     * Constructs an Event.
     */
    public Event(String description, LocalDateTime from, boolean hasFromTime,
                 LocalDateTime to, boolean hasToTime) {
        super(description);
        this.from = from;
        this.hasFromTime = hasFromTime;
        this.to = to;
        this.hasToTime = hasToTime;
    }

    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + getDescription() + " (from: "
                + formatDateTime(from, hasFromTime)
                + " to: "
                + formatDateTime(to, hasToTime)
                + ")" + formatTags();
    }

    @Override
    public String toFileString() {
        String doneFlag = isDone() ? "1" : "0";
        return "E | " + doneFlag + " | " + getDescription() + " | "
                + from + " | " + toFlag(hasFromTime)
                + " | " + to + " | " + toFlag(hasToTime)
                + formatTagsForStorage();
    }

    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = from.toLocalDate();
        LocalDate endDate = to.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Format a LocalDateTime for display taking into account whether a time part was present.
     */
    private String formatDateTime(LocalDateTime value, boolean hasTimePart) {
        if (hasTimePart) {
            return value.format(DATE_TIME_FORMATTER);
        }
        return value.format(DATE_FORMATTER);
    }

    private String toFlag(boolean value) {
        if (value) {
            return "1";
        }
        return "0";
    }
}
