package sallman.task;

import java.time.LocalDate;
import java.util.Optional;

/**
 * A task that runs from one point in time to another,
 * e.g. {@code project meeting} from {@code 2019-10-15} to {@code 2019-10-16}.
 */
public class Event extends Task {

    /** When the event starts. */
    protected LocalDate from;

    /** When the event ends. */
    protected LocalDate to;

    /**
     * Creates an event that is initially not done.
     *
     * @param description what the event involves
     * @param from        when the event starts
     * @param to          when the event ends
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns whether this event is running on the given date.
     * <p>
     * An event covers every day from its start to its end, so a date in the
     * middle counts, not just the two end points.
     *
     * @param date the date being asked about
     * @return true if the event spans that day
     */
    @Override
    public boolean isOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    /**
     * Returns the start date, which is when this event comes up.
     *
     * @return the date the event begins
     */
    @Override
    public Optional<LocalDate> getSortDate() {
        return Optional.of(from);
    }

    /**
     * Returns the start and end dates as saved fields.
     *
     * @return the two dates in the input format, each behind a separator
     */
    @Override
    protected String dateFields() {
        // Saved in the input format, so the file stays readable and
        // reloadable regardless of how dates are displayed.
        return SEPARATOR + from + SEPARATOR + to;
    }

    /**
     * Returns the dates as they are shown, e.g.
     * {@code " (from: Oct 15 2019 to: Oct 16 2019)"}.
     *
     * @return the span in brackets, after the description
     */
    @Override
    protected String details() {
        return " (from: " + TaskDate.format(from)
                + " to: " + TaskDate.format(to) + ")";
    }

    /**
     * Returns an independent copy of this event.
     *
     * @return a event equal to this one, sharing none of its mutable state
     */
    @Override
    public Task copy() {
        return copyInto(new Event(description, from, to));
    }

    /**
     * Returns this event as one line of the data file,
     * e.g. {@code E | 0 | project meeting | 2019-10-15 | 2019-10-16}.
     */
    @Override
    public String toFileFormat() {
        return "E" + SEPARATOR + super.toFileFormat();
    }

    /**
     * Returns this event as it should appear to the user,
     * e.g. {@code [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)}.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString();
    }
}
