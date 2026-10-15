package bob.task;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import bob.BobException;

/**
 * Represents a task that runs from one point in time to another, for example
 * {@code project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)}.
 *
 * <p>As with {@link Deadline}, the start and the end are kept as {@link TaskDateTime}
 * values rather than as the text the user typed, so both are dates the chatbot
 * has understood.
 *
 * <p>A day given with no time is read as the whole of that day: the start of it for
 * {@code from}, the end of it for {@code to}. So an event may run from an hour of a day
 * to the end of that same day.
 *
 * <p>An event may not end before it starts, nor start and end at the same moment.
 * That rule is {@link #requireValidPeriod}, and it is checked on every route by which
 * an event reaches the task list: when one is added, by {@link bob.parser.Parser Parser};
 * when one is edited, by {@link #buildEdited}; and when one is read back from the save
 * file, by {@link bob.storage.Storage Storage}. The constructor does not check it, so
 * that {@code Storage} can report a broken rule in the short wording it uses for every
 * other damaged line of the file.
 */
public class Event extends Task {

    /** The letter that stands for an event, as {@link Todo#TYPE_ICON} does for a todo. */
    public static final String TYPE_ICON = "E";

    /**
     * When the event starts.
     *
     * <p>Private and {@code final}, as {@link Deadline}'s due date is. It matters
     * a little more here, because the two dates are only meaningful as a pair — an
     * event that ended before it started would be nonsense — and a pair that cannot
     * be half-changed cannot fall into that state after being checked.
     */
    private final TaskDateTime from;

    /** When the event ends. */
    private final TaskDateTime to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description what the event is.
     * @param from        when it starts.
     * @param to          when it ends.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Checks that {@code from} and {@code to} can be the start and the end of an event.
     *
     * <p>An end before the start is refused, and both dates are shown back in the
     * friendly form, so a user who typed them the wrong way round can see which was
     * read as which. An end at the same moment as the start is refused too, since an
     * event lasting no time at all is what a deadline records.
     *
     * <p>A day with no time is read as the whole of that day, so it means the start of
     * the day in {@code from} and the end of it in {@code to}. That is what makes
     * {@code 2026-12-05} to {@code 2026-12-05} a whole day rather than nothing, and it
     * lets an event run from an hour of a day to the end of that same day. The pair
     * {@code 2026-12-05} to {@code 2026-12-05 0000} is refused by the rule above: the
     * start counts as midnight, and so does the end the user asked for.
     *
     * <p>Kept in this class rather than in the parser that reads a new event, so that
     * adding, editing and loading an event share one rule and cannot drift apart.
     *
     * @param from when the event would start.
     * @param to   when it would end.
     * @throws BobException if the end comes before the start, or at the same moment
     *                      when both have a time.
     */
    public static void requireValidPeriod(TaskDateTime from, TaskDateTime to) throws BobException {
        if (endsBeforeStart(from, to)) {
            throw new BobException("An event can't end before it starts."
                    + "\nIt would run from " + from + " to " + to + ".");
        }
        if (to.asPeriodEnd().equals(from.asPeriodStart())) {
            throw new BobException("An event can't start and end at the same moment."
                    + " For a single moment, use a deadline.");
        }
    }

    /**
     * Returns whether an event running from {@code from} to {@code to} would be over
     * before it began.
     *
     * <p>Split out of {@link #requireValidPeriod} so that {@link bob.storage.Storage
     * Storage}, which gives its own shorter wording for a damaged line, can tell which
     * of the two broken rules it is looking at without comparing the dates its own way.
     *
     * @param from when the event would start.
     * @param to   when it would end.
     */
    public static boolean endsBeforeStart(TaskDateTime from, TaskDateTime to) {
        return to.asPeriodEnd().isBefore(from.asPeriodStart());
    }

    /** Returns {@link #TYPE_ICON}, the {@code E} that marks an event. */
    @Override
    public String getTypeIcon() {
        return TYPE_ICON;
    }

    /**
     * Returns the start, which is the date an event is pinned to: it is when the
     * event first wants the user's attention, so it is what an event is ordered by
     * and what {@link bob.command.CommandWord#BEFORE CommandWord.BEFORE} and
     * {@link bob.command.CommandWord#AFTER CommandWord.AFTER} measure.
     */
    @Override
    public Optional<TaskDateTime> getScheduledDate() {
        return Optional.of(from);
    }

    /**
     * Returns whether the event is running on {@code day}, counting the day it
     * starts and the day it ends as days it is running.
     *
     * <p>Widened from the inherited single-date test because an event, unlike a
     * deadline, covers a stretch of time. Asking what is on the Wednesday of a
     * week-long orientation should find it, and the inherited version — which
     * compares only the start — would not.
     */
    @Override
    public boolean occursOn(LocalDate day) {
        return !from.isAfter(day) && !to.isBefore(day);
    }

    /**
     * Returns whether {@code other} is an event with the same start and the same end.
     * Both are compared, since two events starting together but ending apart are two
     * different stretches of time.
     */
    @Override
    protected boolean hasSameDatesAs(Task other) {
        return other instanceof Event otherEvent && from.equals(otherEvent.from) && to.equals(otherEvent.to);
    }

    /**
     * Returns an event with the new description, and the new start and end where the
     * edit gives them.
     *
     * <p>The two ends are checked as a pair once the edit has been applied, because
     * moving one end alone can put it on the wrong side of the other, or onto it. The
     * check is made whatever the edit changes: adding and loading an event check the
     * same rule, so an event already in the list always keeps it, and an edit that
     * moves neither end passes.
     */
    @Override
    protected Task buildEdited(String description, TaskEdit edit) throws BobException {
        if (edit.hasDueDate()) {
            throw new BobException("An event has a start and an end, not a due date.");
        }
        TaskDateTime newFrom = Objects.requireNonNullElse(edit.from(), from);
        TaskDateTime newTo = Objects.requireNonNullElse(edit.to(), to);
        requireValidPeriod(newFrom, newTo);
        return new Event(description, newFrom, newTo);
    }

    /**
     * Returns for example
     * {@code [E][ ] project meeting (from: Aug 06 2026 14:00 to: Aug 06 2026 16:00)}.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Adds the start and the end after the three fields every task saves.
     *
     * <p>They are added as two fields rather than joined into one, so that reading
     * them back is a matter of taking two fields apart rather than of splitting a
     * single field down the middle.
     */
    @Override
    public List<String> toSaveFields() {
        List<String> fields = super.toSaveFields();
        fields.add(from.toSaveField());
        fields.add(to.toSaveField());
        return fields;
    }
}
