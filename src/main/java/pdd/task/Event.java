package pdd.task;

import java.time.LocalDate;
import java.util.Optional;

/** A task that spans from a specific date to a (freeform) end time/date. */
public class Event extends Task {
    protected LocalDate from;
    protected String to;

    /** Creates a not-done event with the given description, start date, and freeform end text. */
    public Event(String description, LocalDate from, String to) {
        super(description);
        assert from != null : "from must not be null; toString() and occursOn() rely on it";
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT) + " to: " + to + ")";
    }

    /** Serializes this task to a single line in the save-file format. */
    public String toFileFormat() {
        return "E" + FIELD_SEPARATOR + getStatusValue() + FIELD_SEPARATOR + description
                + FIELD_SEPARATOR + from + FIELD_SEPARATOR + to;
    }

    /** {@inheritDoc} An event occurs on its start date. */
    @Override
    public boolean occursOn(LocalDate date) {
        return from.equals(date);
    }

    /** {@inheritDoc} An event sorts by its start date. */
    @Override
    public Optional<LocalDate> getSortDate() {
        return Optional.of(from);
    }
}
