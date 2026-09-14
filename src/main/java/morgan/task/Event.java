package morgan.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a task that needs to be completed before specified starts and end time.
 */
public class Event extends Task {
    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", Locale.ENGLISH);
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private LocalDateTime start;
    private LocalDateTime end;

    /**
     * Constructs an event task with specified description, start date and end date.
     *
     * @param name     The textual description of the event task.
     * @param startStr The start date of the task.
     * @param endStr   The end date of the task.
     * @throws DateTimeParseException If the date is in invalid format.
     */
    public Event(String name, String startStr, String endStr) throws DateTimeParseException {
        super(name);
        this.start = LocalDateTime.parse(startStr.trim(), INPUT_FORMATTER);
        this.end = LocalDateTime.parse(endStr.trim(), INPUT_FORMATTER);
    }

    /**
     * Gets the start date of the task.
     *
     * @return The start date of the task.
     */
    public LocalDateTime getStart() {
        return this.start;
    }

    /**
     * Checks if a task happens on the date.
     *
     * @param date A date entered by the user.
     * @return True if the task is happened on that dates, otherwise false.
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Gets the end date of the task.
     *
     * @return The end date of the task.
     */
    public LocalDateTime getEnd() {
        return this.end;
    }

    /**
     * Gets the start date of the task from the storage file.
     *
     * @return The start date of the task.
     */
    public String getStartForStorage() {
        return this.start.format(INPUT_FORMATTER);
    }

    /**
     * Gets the end date of the task from the storage file.
     *
     * @return The end date of the task.
     */
    public String getEndForStorage() {
        return this.end.format(INPUT_FORMATTER);
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)",
                super.toString(),
                start.format(OUTPUT_FORMATTER),
                end.format(OUTPUT_FORMATTER));
    }
}
