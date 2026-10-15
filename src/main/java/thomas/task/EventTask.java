package thomas.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import thomas.ThomasException;

/**
 * A task that runs from one date and time to another.
 * <p>
 * Like {@link DeadlineTask}, the start and end are kept as
 * {@link LocalDateTime}s: read in as {@code yyyy-mm-dd HHmm} and shown back in
 * a friendlier form, using the formats {@link Task} holds for both.
 */
public class EventTask extends Task {
    /** When the event starts, as given after {@code /from}. */
    protected LocalDateTime from;

    /** When the event ends, as given after {@code /to}. */
    protected LocalDateTime to;

    /**
     * Creates an event that is not yet done.
     * <p>
     * An event that ends before it starts is refused here rather than in the
     * command that reads it, so there is no way to make one at all: the save
     * file is loaded through this constructor too, so a line edited by hand to
     * run backwards is reported and skipped instead of loading an event no
     * command could have created.
     * <p>
     * A start equal to the end is allowed. An event lasting no time is odd but
     * says nothing false, while one ending before it begins cannot be true.
     *
     * @param description The task text, without the {@code event} keyword.
     * @param from The date and time the event starts.
     * @param to The date and time the event ends, not before {@code from}.
     * @throws ThomasException If {@code to} falls before {@code from}.
     */
    public EventTask(String description, LocalDateTime from, LocalDateTime to)
            throws ThomasException {
        super(description);
        if (from.isAfter(to)) {
            throw new ThomasException("Bust my buffers! Your event arrives before it sets off! "
                    + "Check its /from and /to.");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event with its type tag and time range.
     *
     * @return For example
     *         {@code "[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)"}.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DATE_DISPLAY_FORMAT)
                + " to: " + to.format(DATE_DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns whether this event is running on a given day.
     * <p>
     * An event covers every day from its start to its end, so this is a range
     * test rather than a match against the start alone: an event running from
     * the 2nd to the 4th is happening on the 3rd as much as on the 2nd.
     * <p>
     * Both ends count as part of the event, which is why the test is written as
     * two negated comparisons. The reading that first suggests itself,
     * {@code day.isAfter(start) && day.isBefore(end)}, excludes the first and
     * last day of every event -- a mistake that still passes any test where the
     * day asked about falls in the middle.
     *
     * @param day The day being asked about.
     * @return True if the event is running on that day.
     */
    @Override
    public boolean occursOn(LocalDate day) {
        LocalDate start = from.toLocalDate();
        LocalDate end = to.toLocalDate();
        // The range test below reads as a range only because the constructor
        // refuses an event that ends before it starts. Were that to change,
        // this would quietly answer no for every day rather than failing:
        // an event that never matches is far harder to notice than a crash.
        assert !end.isBefore(start) : "Event ends on " + end + ", before it starts on " + start;
        return !day.isBefore(start) && !day.isAfter(end);
    }

    /**
     * Returns whether another object is this event written a second time: the
     * same description, as {@link Task#equals(Object)} checks, running between
     * the same two moments.
     *
     * @param other The object to compare with.
     * @return True if {@code other} is an event with the same description,
     *         start and end.
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        EventTask otherEvent = (EventTask) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to);
    }

    /**
     * Returns the event encoded for the save file, tagged {@code E} with the
     * start and end as the fourth and fifth fields.
     * <p>
     * As in {@link DeadlineTask}, the dates are written in the format they are
     * typed in, so the parser can read back whatever is saved.
     *
     * @return For example {@code "E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600"}.
     */
    @Override
    public String toSaveFormat() {
        return "E" + FIELD_SEPARATOR + super.toSaveFormat()
                + FIELD_SEPARATOR + from.format(DATE_INPUT_FORMAT)
                + FIELD_SEPARATOR + to.format(DATE_INPUT_FORMAT);
    }
}
