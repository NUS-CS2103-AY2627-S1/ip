package peter.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an event occurring between two dates and times.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an incomplete event.
     *
     * <p>This constructor does not check that {@code from} is no later than
     * {@code to}; {@code Parser} rejects a backwards event before reaching
     * here, because such an event would cover no dates at all.
     *
     * @param description what the task says.
     * @param from date and time the event starts.
     * @param to date and time the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns both ends of the event in brackets. Times are included only
     * when they are not at midnight.
     */
    @Override
    public String getScheduleDetails() {
        return " (from: " + ScheduleDateTime.format(from)
                + " to: " + ScheduleDateTime.format(to) + ")";
    }

    /**
     * {@inheritDoc}
     *
     * <p>An event stores both of its ends, the start before the end.
     */
    @Override
    protected List<String> getScheduleDataFields() {
        return List.of(from.toString(), to.toString());
    }

    /**
     * Returns whether the event is running on the given date.
     *
     * <p>The range is inclusive at both ends and compares dates only, so a
     * multi-day event matches every date it spans regardless of the times of
     * day it starts and finishes.
     *
     * @param date date to check.
     * @return whether the event runs on the date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = from.toLocalDate();
        LocalDate endDate = to.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
