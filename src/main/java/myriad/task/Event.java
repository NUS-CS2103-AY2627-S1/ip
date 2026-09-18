package myriad.task;

import java.time.LocalDateTime;

import myriad.MyriadException;

/**
 * A task that spans a start and an end date/time. Unlike a {@link Deadline},
 * which occurs at one point, an {@code Event} covers everything between its
 * two endpoints, which is what {@link #overlaps} compares against.
 */
public class Event extends Task {

    /** When the event starts. */
    private final TaskDateTime start;

    /** When the event ends; see {@link #checkTimesInOrder} for how it relates to {@link #start}. */
    private final TaskDateTime end;

    /**
     * Creates a not-done {@code Event}. The two times are taken as given, so
     * callers building one from user input or saved data should first check
     * them with {@link #checkTimesInOrder}; the constructor does not, so that
     * it does not have to declare a checked exception for every caller.
     *
     * @param description what the event is.
     * @param start       when it starts, already parsed.
     * @param end         when it ends, already parsed.
     */
    public Event(String description, TaskDateTime start, TaskDateTime end) {
        super(description);
        // Start before end is not asserted: the times come from user input, so
        // they are checked with checkTimesInOrder, which reports a proper error.
        assert start != null && end != null : "TaskDateTime.parse never returns null";

        this.start = start;
        this.end = end;
    }

    /**
     * Checks that an event with these times ends after it starts, so that an
     * impossible event is rejected with an explanation rather than stored.
     * An event whose start and end are the same instant is rejected too,
     * since it would not last any time.
     *
     * A date given without a time stands for its whole day, so the check
     * compares the earliest instant of {@code start} with the latest instant
     * of {@code end}. That keeps a one-day event such as
     * {@code /from 2019-12-02 /to 2019-12-02} valid.
     *
     * @param start when the event starts.
     * @param end   when the event ends.
     * @throws MyriadException if the end is not after the start.
     */
    public static void checkTimesInOrder(TaskDateTime start, TaskDateTime end) throws MyriadException {
        if (!start.rangeStart().isBefore(end.rangeEnd())) {
            throw new MyriadException(String.format(
                    "An event must end after it starts, but it ends (%s) before or when it starts (%s).",
                    end, start));
        }
    }

    /**
     * Returns whether {@code other} is an event with the same description and
     * the same start and end. See {@link Task#hasSameDetails}.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }
        // Same type is checked above, so this cast cannot fail.
        Event otherEvent = (Event) other;
        return start.equals(otherEvent.start) && end.equals(otherEvent.end);
    }

    @Override
    public TaskType getType() {
        return TaskType.EVENT;
    }

    /**
     * Returns this task's save-format line prefixed with its type code and
     * suffixed with the raw start and end times, so the type and both times
     * can be recovered when the data file is read back in.
     */
    @Override
    public String toSaveFormat() {
        return String.format("%s | %s | %s | %s",
                getType().getCode(), super.toSaveFormat(), start.toSaveFormat(), end.toSaveFormat());
    }

    /**
     * Returns the display form, e.g.
     * {@code [E][ ] meeting (from: Dec 02 2019 1400 to: Dec 02 2019 1600)},
     * with both times in {@link TaskDateTime}'s display format, not the save
     * format.
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), start, end);
    }

    /**
     * Matches if the period from {@code from} to {@code to} overlaps this
     * event's span, from the earliest instant of {@link #start} to the latest
     * instant of {@link #end}. See {@link TaskDateTime#rangesOverlap} for the
     * general rule.
     */
    @Override
    public boolean overlaps(LocalDateTime from, LocalDateTime to) {
        return TaskDateTime.rangesOverlap(
                start.rangeStart(), end.rangeEnd(), from, to);
    }
}
