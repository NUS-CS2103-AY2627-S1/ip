package dukey.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dukey.exception.DukeyException;

/**
 * Represents a task that occurs from one date/time to another.
 */
public class Event extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates an event task with the given description and date/time range.
     *
     * @param description description of the event task.
     * @param from start date/time of the event.
     * @param to end date/time of the event.
     * @throws DukeyException if the start date/time is after the end date/time.
     */
    public Event(String description, String from, String to) throws DukeyException {
        super(description);
        this.from = LocalDateTime.parse(from, STORAGE_DATE_TIME_FORMAT);
        this.to = LocalDateTime.parse(to, STORAGE_DATE_TIME_FORMAT);

        if (this.from.isAfter(this.to)) {
            throw new DukeyException("Event start date/time cannot be later than end date/time.");
        }
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + formatForDisplay(this.from)
                + " to: " + formatForDisplay(this.to) + ")";
    }

    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = this.from.toLocalDate();
        LocalDate endDate = this.to.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public String toFileString() {
        return String.format("E | %d | %s | %s | %s |%s", this.isDone ? 1 : 0, this.description,
                this.from.format(STORAGE_DATE_TIME_FORMAT), this.to.format(STORAGE_DATE_TIME_FORMAT),
                getDoneAtStorageText());
    }

    private String formatForDisplay(LocalDateTime dateTime) {
        return dateTime.format(OUTPUT_FORMAT)
                .replace("AM", "am")
                .replace("PM", "pm");
    }
}
