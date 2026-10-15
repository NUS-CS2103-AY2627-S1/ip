package clarry.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import clarry.ClarryException;

/**
 * Represents a task scheduled between a start and end time.
 */
public class Event extends Task {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an incomplete event task.
     *
     * @param description description of the task
     * @param from event start date and time in {@code yyyy-MM-dd HH:mm} format
     * @param to event end date and time in {@code yyyy-MM-dd HH:mm} format
     * @throws ClarryException if either date and time is invalid, or the end is not after the start
     */
    public Event(String description, String from, String to) throws ClarryException {
        super(description);
        try {
            this.from = LocalDateTime.parse(from, INPUT_FORMAT);
            this.to = LocalDateTime.parse(to, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ClarryException(
                    "Please use yyyy-mm-dd HH:mm for event dates, e.g. 2019-10-15 14:00.");
        }

        if (!this.to.isAfter(this.from)) {
            throw new ClarryException("An event must end after it starts.");
        }
        assert this.to.isAfter(this.from) : "A valid event must end after it starts";
    }

    /**
     * Returns this event in the format shown in Clarry's task list.
     *
     * @return formatted event description, completion status, and schedule
     */
    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + getDescription()
                + " (from: " + from.format(OUTPUT_FORMAT) + " to: " + to.format(OUTPUT_FORMAT) + ")";
    }

    /**
     * Returns this event in Clarry's persistent storage format.
     *
     * @return pipe-separated event data
     */
    @Override
    public String toFileFormat() {
        return "E | " + (isDone() ? "1" : "0") + " | " + getDescription()
                + " | " + from.format(INPUT_FORMAT) + " | " + to.format(INPUT_FORMAT);
    }

    /**
     * Returns whether this event overlaps the specified calendar date.
     *
     * @param date date to check
     * @return whether any part of this event occurs on the date
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }
}
