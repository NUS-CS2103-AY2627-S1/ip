package maggigorengayam.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import maggigorengayam.util.DateTimeUtil;

/** A task spanning a "from" date/time to a "to" date/time. */
public class Event extends Task {
    protected LocalDate fromDate;
    protected LocalTime fromTime;
    protected LocalDate toDate;
    protected LocalTime toTime;

    /** Creates an event with the given description and from/to dates; either time may be {@code null}. */
    public Event(String description, LocalDate fromDate, LocalTime fromTime, LocalDate toDate, LocalTime toTime) {
        super(description);
        // Unlike fromTime/toTime, both dates are required: both call sites (Parser, Storage)
        // only ever reach this constructor with dates DateTimeUtil#parse already succeeded on.
        assert fromDate != null && toDate != null
                : "fromDate and toDate must not be null; only the times are optional";
        this.fromDate = fromDate;
        this.fromTime = fromTime;
        this.toDate = toDate;
        this.toTime = toTime;
    }

    /** The date this event starts on. */
    public LocalDate getFromDate() {
        return fromDate;
    }

    /** The time of day this event starts, or {@code null} if only a date was given. */
    public LocalTime getFromTime() {
        return fromTime;
    }

    /** The date this event ends on. */
    public LocalDate getToDate() {
        return toDate;
    }

    /** The time of day this event ends, or {@code null} if only a date was given. */
    public LocalTime getToTime() {
        return toTime;
    }

    /** True when {@code date} falls within [fromDate, toDate], inclusive of both ends. */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(fromDate) && !date.isAfter(toDate);
    }

    /** Same as {@link Task#hasSameDetailsAs}, additionally requiring identical "from"/"to" dates and times. */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        if (!super.hasSameDetailsAs(other)) {
            return false;
        }
        Event that = (Event) other;
        return fromDate.equals(that.fromDate) && Objects.equals(fromTime, that.fromTime)
                && toDate.equals(that.toDate) && Objects.equals(toTime, that.toTime);
    }

    /** e.g. "[E][ ] project meeting (from: Dec 2 2019, 2pm to: Dec 2 2019, 4pm)". */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeUtil.formatForDisplay(fromDate, fromTime)
                + " to: "
                + DateTimeUtil.formatForDisplay(toDate, toTime)
                + ")";
    }

    /** e.g. "E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600". */
    @Override
    public String toSaveFormat() {
        return "E | " + getStatusFlag() + " | " + description + " | "
                + DateTimeUtil.formatForSave(fromDate, fromTime) + " | "
                + DateTimeUtil.formatForSave(toDate, toTime);
    }
}
