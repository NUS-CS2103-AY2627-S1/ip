package eve.task;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/** A task that starts on a specific date and ends on a specific date. */
public class Event extends Task {
    protected LocalDate from;
    protected LocalDate to;

    /**
     * Creates an event task.
     *
     * @param description what the task is.
     * @param from the date it starts.
     * @param to the date it ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns this task's display text, prefixed with "[E]" and suffixed with its start/end dates. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + from + " | " + to;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    /** Returns this event's start date, so it sorts by when it begins. */
    @Override
    public Optional<LocalDate> getScheduleDate() {
        return Optional.of(from);
    }

    /** Also compares the start/end dates, on top of the type/description check {@link Task#equals} does. */
    @Override
    public boolean equals(Object other) {
        return super.equals(other) && from.equals(((Event) other).from) && to.equals(((Event) other).to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to);
    }
}
