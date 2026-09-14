package nova.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import nova.DateTimes;

/**
 * A task that occupies a stretch of time, from a start moment to an end
 * moment. The two may fall on different days.
 */
public class Event extends Task {
    /** When the event starts. Midnight means the user gave only a date. */
    private final LocalDateTime from;

    /** When the event ends. Midnight means the user gave only a date. */
    private final LocalDateTime to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description what the user called this task
     * @param from        when it starts
     * @param to          when it ends
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }


    /**
     * Returns whether this event is running on the given date, i.e. the date
     * lies anywhere between its start day and its end day inclusive.
     *
     * @param date the date to test against
     * @return true if the event spans that date
     */
    @Override
    public boolean isOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    /**
     * Returns when this event starts, which is the moment it sorts by. The
     * start is used rather than the end so that events appear in the order
     * the user will encounter them.
     *
     * @return the start date and time
     */
    @Override
    public Optional<LocalDateTime> getScheduledTime() {
        return Optional.of(from);
    }

    @Override
    protected String identity() {
        return super.identity() + "|" + from + "|" + to;
    }

    @Override
    public String toFileString() {
        return "E | " + super.toFileString()
                + " | " + DateTimes.toFileString(from)
                + " | " + DateTimes.toFileString(to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimes.format(from)
                + " to: " + DateTimes.format(to) + ")";
    }
}
