package auto.task;

import java.time.LocalDate;

import auto.util.DateUtil;

/** Represents a task that occurs over an inclusive date range. */
public class Event extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /** Creates an incomplete event with the specified description and date range. */
    public Event(String name, LocalDate startDate, LocalDate endDate) {
        super(name);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** Includes both the event's start date and end date. */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public String toDataString() {
        return String.format("E | %d | %s | %s | %s", isCompleted() ? 1 : 0,
                encode(getName()), encode(DateUtil.format(startDate)), encode(DateUtil.format(endDate)));
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(),
                DateUtil.format(startDate), DateUtil.format(endDate));
    }
}
