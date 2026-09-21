package thaisbot.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A task that has a deadline (single point in time).
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy HHmm");

    private final LocalDateTime by;
    private final boolean hasTime;

    /**
     * Constructs a Deadline.
     * @param description description text
     * @param by the deadline date/time
     * @param hasTime whether the original input included a time
     */
    public Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        this.by = by;
        this.hasTime = hasTime;
    }

    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + getDescription()
                + " (by: " + formatDateTime(by, hasTime) + ")" + formatTags();
    }

    @Override
    public String toFileString() {
        String doneFlag = isDone() ? "1" : "0";
        return "D | " + doneFlag + " | " + getDescription() + " | " + by + " | " + toFlag(hasTime)
                + formatTagsForStorage();
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /**
     * Formats the stored LocalDateTime for display.
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
