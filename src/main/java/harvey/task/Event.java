package harvey.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import harvey.HarveyException;
/**
 * A task that runs from one point in time to another,
 * e.g. {@code project meeting (from: Oct 15 2019 2:00PM to: Oct 15 2019 4:00PM)}.
 * <p>
 * Both ends are held as {@link LocalDateTime} rather than as text, for the same reason a
 * {@link Deadline} holds a real date: so the program understands the times instead of
 * merely repeating them back. Here it also buys something a deadline does not need. An
 * event occupies a stretch of time rather than a single instant, so two events can
 * collide, and only real times make that collision something the program can notice. See
 * {@link #overlaps(Event)}.
 */
public class Event extends Task {
    /**
     * How a time is shown to the user, e.g. {@code Oct 15 2019 2:00PM}.
     * Deliberately different from the input format, to make the point that the stored
     * value is a moment in time and not the text that was typed.
     * <p>
     * The locale is fixed to English, as in {@link Deadline}. Both the month name and the
     * AM/PM marker would otherwise follow the language of the computer.
     */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy h:mma", Locale.ENGLISH);

    /**
     * The format the user types and the save file uses, e.g. {@code 2019-10-15 1400}.
     * <p>
     * Resolved strictly, so that an impossible time such as {@code 2400} is rejected rather
     * than rolled forward into the next midnight, which is what the default resolver does.
     * Strict resolving needs {@code uuuu} rather than {@code yyyy}, because {@code yyyy}
     * means the year within an era and so cannot be resolved without knowing the era.
     */
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);

    /** The same format written the way it is described to the user. */
    private static final String INPUT_FORMAT_DESCRIPTION = "yyyy-mm-dd HHmm";

    /** When the event starts. */
    protected LocalDateTime from;

    /** When the event ends. Always strictly after {@link #from}. */
    protected LocalDateTime to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description the text describing the task.
     * @param from        when the event starts.
     * @param to          when the event ends, which must be after it starts.
     * @throws HarveyException if the event does not end after it starts.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) throws HarveyException {
        super(description);
        // Checked here rather than at the one call site that reads user input, because the
        // save file is a second way in and can be edited by hand. An event that ends before
        // it starts would occupy a negative stretch of time, which overlaps nothing and so
        // would slip past the clash check unnoticed.
        if (!to.isAfter(from)) {
            throw new HarveyException("An event has to end after it starts, and \""
                    + to.format(INPUT_FORMAT) + "\" is not after \"" + from.format(INPUT_FORMAT) + "\".");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Turns typed text into a moment in time.
     * <p>
     * This lives here, next to the fields it produces, so that the one place that decides
     * what a valid time looks like is shared by both the command parser and the save file
     * reader instead of being written out twice.
     *
     * @param text the time as typed, expected in {@code yyyy-mm-dd HHmm} form.
     * @return the moment it represents.
     * @throws HarveyException if the text is not a time in that form.
     */
    public static LocalDateTime parseDateTime(String text) throws HarveyException {
        try {
            return LocalDateTime.parse(text, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            // DateTimeParseException is Java's complaint; it is replaced with advice the
            // user can act on, and routed through the same channel as every other problem.
            throw new HarveyException("I could not read \"" + text + "\" as a date and time. "
                    + "Please write it as " + INPUT_FORMAT_DESCRIPTION
                    + ", for example 2019-10-15 1400.");
        }
    }

    /**
     * {@inheritDoc}
     * An event also needs the same start and end: a weekly lesson on two dates is two tasks.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        // super checks the class first, so the cast below is safe once it has passed.
        if (!super.hasSameDetails(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }

    /**
     * Returns whether this event and another one share any time at all.
     * <p>
     * Both comparisons are strict, so events that merely touch do not overlap: one running
     * 4pm to 6pm and another running 6pm to 8pm are back to back, not in conflict. That is
     * the useful reading for a schedule, where consecutive bookings are normal.
     *
     * @param other the event to compare against.
     * @return true if the two stretches of time intersect.
     */
    public boolean overlaps(Event other) {
        assert other != null : "Callers compare against events already in the list, never null";
        return from.isBefore(other.to) && other.from.isBefore(to);
    }

    /**
     * {@inheritDoc}
     * Wraps the inherited form with the {@code [E]} marker and both times in the display
     * format, giving e.g. {@code [E][ ] project meeting (from: Oct 15 2019 2:00PM to: Oct 15 2019 4:00PM)}.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * {@inheritDoc}
     * Prefixes the {@code E} type letter and appends both times,
     * giving e.g. {@code E | 0 | project meeting | 2019-10-15 1400 | 2019-10-15 1600}.
     * <p>
     * The times are written in the input format rather than with {@code toString()}, so
     * that the file can be read back by the same {@link #parseDateTime(String)} that reads
     * what the user types. {@link Deadline} gets that property for free, because a
     * {@code LocalDate} prints itself in exactly the form it parses; a {@code LocalDateTime}
     * does not, so the format is applied here explicitly.
     */
    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from.format(INPUT_FORMAT)
                + " | " + to.format(INPUT_FORMAT);
    }
}
