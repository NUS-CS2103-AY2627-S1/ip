package tally.task;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A task that runs from one moment to another.
 *
 * <p>Both ends are days, either of which may carry an hour. An event is happening on
 * every day it touches, so none of them can be offered as free, where a window is a
 * period the work may be done within. An event that ends at midnight is over before the
 * day its end names, and leaves that day free.
 */
public class Event extends Task {
    /** The letter standing for this kind of task in the data file. */
    public static final String TYPE = "E";

    /** When the event starts. */
    private final Moment start;

    /** When it ends, never before {@link #start}. */
    private final Moment end;

    /**
     * Creates an event that is not done yet.
     *
     * @param description what is happening.
     * @param start when it starts.
     * @param end when it ends, at or after the start.
     */
    public Event(String description, Moment start, Moment end) {
        super(description);
        assert !hasBackwardsEnds(start, end)
                : "Parser.parseEvent and TaskLine.readEvent both refuse an event that ends"
                + " before it starts, so one reaching here came from neither: "
                + start + " to " + end;
        this.start = start;
        this.end = end;
    }

    /**
     * Returns whether an event running between two moments would end before it started.
     *
     * <p>An end given without an hour runs to the close of its day, so "the 12th at four
     * to the 12th" is a half-day event rather than a contradiction. The comparison lives
     * here so that the parser, the data file's reader and this class all answer it the
     * same way.
     *
     * @param start when it would start.
     * @param end when it would end.
     * @return true when the end falls before the start.
     */
    public static boolean hasBackwardsEnds(Moment start, Moment end) {
        return end.asEnd().isBefore(start.asStart());
    }

    /**
     * Returns whether this event takes up a given day.
     *
     * <p>A day an event touches at all is a day it takes up: an hour of it is enough to
     * make the day something other than free.
     *
     * @param day the day being considered.
     * @return true when the day falls between the day the event starts and the last day
     *     it is on, both included.
     */
    @Override
    public boolean occupies(LocalDate day) {
        return !day.isBefore(start.date()) && !day.isAfter(findLastDayTakenUp());
    }

    /**
     * Returns the last day this event is on, which is not always the day its end names.
     *
     * <p>Midnight is the close of one day rather than a moment of the next, so an event
     * running to 00:00 on the 10th leaves the whole of the 10th free. There being no
     * 24:00 to write instead, that is the only way to say "until midnight", and reading
     * it as a day taken would cost a free day for the sake of no minutes at all.
     *
     * <p>An event that both starts and ends at midnight on one day keeps that day, since
     * it has no other to fall back to.
     *
     * @return the last day the event takes up.
     */
    private LocalDate findLastDayTakenUp() {
        boolean endsAtMidnight = end.time().filter(LocalTime.MIDNIGHT::equals).isPresent();
        if (endsAtMidnight && end.date().isAfter(start.date())) {
            return end.date().minusDays(1);
        }
        return end.date();
    }

    /**
     * Returns whether this event records the same thing as another task.
     *
     * <p>A task is otherwise told apart by the line it would be saved as, which for an
     * event is not enough: a start written "2026-09-08" and one written
     * "2026-09-08 00:00" are two lines naming one moment, and two events built from
     * them run over exactly the same time. Comparing the moments rather than the text
     * is what keeps the second from being added alongside the first.
     *
     * @param other the task to compare with.
     * @return true when the two are events over the same stretch of the same thing.
     */
    @Override
    public boolean isSameAs(Task other) {
        if (!(other instanceof Event event)) {
            return false;
        }
        return getDescription().equals(event.getDescription())
                && start.asStart().equals(event.start.asStart())
                && end.asEnd().equals(event.end.asEnd());
    }

    /**
     * Returns this event as the user sees it, tagged with its type and its span.
     *
     * @return for example "[E][ ] lecture (from: Sep 12 2026 4:00 pm to: Sep 12 2026
     *     6:00 pm)".
     */
    @Override
    public String toString() {
        return String.format("[%s]%s (from: %s to: %s)", TYPE, super.toString(), start, end);
    }

    /**
     * Returns the line standing for this event in the data file.
     *
     * @return for example "E | 0 | lecture | 2026-09-12 16:00 | 2026-09-12 18:00".
     */
    @Override
    public String toSaveFormat() {
        return TYPE + FIELD_SEPARATOR + toSharedSaveFields()
                + FIELD_SEPARATOR + start.toSaveFormat() + FIELD_SEPARATOR + end.toSaveFormat();
    }
}
