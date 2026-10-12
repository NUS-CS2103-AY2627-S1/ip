package echo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

import echo.EchoException;
import echo.util.DateTimeParser;

/**
 * Represents a task with a start date and an end date.
 */
public class EventTask extends Task {
    /** Event start date stored as a Java date value. */
    private final LocalDate from;

    /** Optional event start time stored as a Java time value. */
    private final LocalTime fromTime;

    /** Event end date stored as a Java date value. */
    private final LocalDate to;

    /** Optional event end time stored as a Java time value. */
    private final LocalTime toTime;

    /**
     * Creates an event task.
     *
     * @param description event description.
     * @param from event start date with an optional time.
     * @param to event end date with an optional time.
     * @throws EchoException if the event dates or times are out of chronological order.
     */
    public EventTask(String description, String from, String to) throws EchoException {
        super(description);
        DateTimeParser.DateTimeValue fromDateTime = DateTimeParser.parse(from);
        DateTimeParser.DateTimeValue toDateTime = DateTimeParser.parse(to);
        DateTimeParser.validateChronologicalOrder(fromDateTime, toDateTime);
        this.from = fromDateTime.date();
        this.fromTime = fromDateTime.time();
        this.to = toDateTime.date();
        this.toTime = toDateTime.time();
    }

    @Override
    public boolean isDuplicate(Task other) {
        if (!super.isDuplicate(other) || !(other instanceof EventTask otherEvent)) {
            return false;
        }
        return from.equals(otherEvent.from)
                && Objects.equals(fromTime, otherEvent.fromTime)
                && to.equals(otherEvent.to)
                && Objects.equals(toTime, otherEvent.toTime);
    }

    @Override
    public Optional<LocalDateTime> getDateTime() {
        return Optional.of(LocalDateTime.of(from, fromTime != null ? fromTime : LocalTime.MIDNIGHT));
    }

    @Override
    public String toFileFormat() {
        return "E | " + getDoneFlag() + " | " + getDescription() + " | "
                + DateTimeParser.formatForStorage(from, fromTime) + " | "
                + DateTimeParser.formatForStorage(to, toTime);
    }

    @Override
    public String toString() {
        return "[Event]" + super.toString() + " (from: "
                + DateTimeParser.formatForDisplay(from, fromTime) + ", to: "
                + DateTimeParser.formatForDisplay(to, toTime) + ")";
    }
}
