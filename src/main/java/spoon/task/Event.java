package spoon.task;

import java.time.LocalDate;

import spoon.exception.InvalidArgumentException;
import spoon.exception.InvalidFormatException;
import spoon.util.DateFormat;

/**
 * Represents an Event (subclass of Task) with additional fields startDate and endDate.
 */
public class Event extends Task {
    private final DateFormat.ParseResult startDate;
    private final DateFormat.ParseResult endDate;

    // Constructor
    public Event(String name, String startDate, String endDate) throws
            InvalidFormatException, InvalidArgumentException {
        super(name);
        this.startDate = DateFormat.parse(startDate);
        this.endDate = DateFormat.parse(endDate);
        if (this.startDate.dateTime().isAfter(this.endDate.dateTime())) {
            throw new InvalidArgumentException("Start time must be before end time!");
        }
        assert !(this.startDate.dateTime().isAfter(this.endDate.dateTime()))
                : "Start time should never be after end time";
    }

    // Methods
    @Override
    public boolean isDueOn(LocalDate date) {
        return DateFormat.isOccurringOn(startDate.dateTime(), endDate.dateTime(), date);
    }

    @Override
    public boolean isDueBy(LocalDate date) {
        return DateFormat.isDueBy(startDate.dateTime(), date);
    }

    @Override
    public String format() {
        return "E | " + super.format()
                + " | " + DateFormat.toStorage(startDate)
                + " | " + DateFormat.toStorage(endDate);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateFormat.toDisplay(startDate)
                + " to: " + DateFormat.toDisplay(endDate) + ")";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Event otherEvent)) {
            return false;
        }
        return super.equals(object) && java.util.Objects.equals(this.startDate, otherEvent.startDate)
                && java.util.Objects.equals(this.endDate, otherEvent.endDate);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), this.startDate, this.endDate);
    }
}
