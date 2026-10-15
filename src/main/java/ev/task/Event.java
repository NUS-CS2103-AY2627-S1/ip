package ev.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ev.DateTimes;
import ev.EvException;

/**
 * A task that runs from one date and time to another.
 */
public class Event extends Task {

    /** One-letter type of this task in the save file. */
    public static final String TYPE = "E";

    /** Option the user types, both to set and to change when the event starts. */
    public static final String OPTION_FROM = "/from";

    /** Option the user types, both to set and to change when the event ends. */
    public static final String OPTION_TO = "/to";

    /** When the event starts. */
    protected LocalDateTime from;

    /** When the event ends. */
    protected LocalDateTime to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description what the user typed as the description of the task.
     * @param from when the event starts.
     * @param to when the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) throws EvException {
        super(description);
        requireOrderedTimes(from, to);
        this.from = from;
        this.to = to;
    }

    /**
     * Checks that an event does not end before it starts.
     *
     * <p>Both ends are checked here rather than at each place that sets one, so that an
     * event cannot reach an impossible state whether it is being created, loaded from the
     * save file, or edited one end at a time.
     *
     * @param from when the event starts.
     * @param to when the event ends.
     * @throws EvException if the end comes before the start.
     */
    private static void requireOrderedTimes(LocalDateTime from, LocalDateTime to) throws EvException {
        if (to.isBefore(from)) {
            throw new EvException("An event cannot end before it starts.",
                    "It would run from " + DateTimes.format(from) + " to " + DateTimes.format(to) + ".");
        }
    }

    /**
     * Returns whether this event is running on the given date.
     *
     * <p>An event that spans several days counts as happening on its first day,
     * its last day and every day in between.
     *
     * @param date the date being asked about.
     * @return true if the event covers that date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    @Override
    public void applyUpdate(String option, String value) throws EvException {
        if (option.equals(OPTION_FROM)) {
            LocalDateTime newFrom = DateTimes.parse(value);
            requireOrderedTimes(newFrom, to);
            from = newFrom;
            return;
        }
        if (option.equals(OPTION_TO)) {
            LocalDateTime newTo = DateTimes.parse(value);
            requireOrderedTimes(from, newTo);
            to = newTo;
            return;
        }
        super.applyUpdate(option, value);
    }

    @Override
    protected String describeDetails() {
        return super.describeDetails() + FIELD_SEPARATOR + from + FIELD_SEPARATOR + to;
    }

    @Override
    protected String getTypeName() {
        return "an event";
    }

    @Override
    protected String listUpdatableOptions() {
        return super.listUpdatableOptions() + ", " + OPTION_FROM + ", " + OPTION_TO;
    }

    @Override
    public String toFileFormat() {
        return encode(TYPE, DateTimes.toFileFormat(from), DateTimes.toFileFormat(to));
    }

    @Override
    public String toString() {
        return "[" + TYPE + "]" + super.toString()
                + " (from: " + DateTimes.format(from) + " to: " + DateTimes.format(to) + ")";
    }
}
