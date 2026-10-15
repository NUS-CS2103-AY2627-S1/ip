package piplupbot.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import piplupbot.PiplupBotException;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents a task that runs from one point in time to another,
 * e.g. {@code team project meeting 2019-10-02 1400 to 1600}.
 */
public class Event extends Task {
    /**
     * When the event starts, as a real point in time.
     * {@code private final} for the reason {@link Deadline}'s due date is: no
     * subclass shares it, and a date accepted once should not be replaceable.
     */
    private final LocalDateTime from;

    /** When the event ends, as a real point in time, and likewise unchangeable. */
    private final LocalDateTime to;

    /**
     * Creates an event that is not done yet.
     *
     * <p>The event must end after it starts. That is checked here rather than
     * where the typed line is read, so that an event loaded from the save file is
     * held to the same rule: a hand-edited line that breaks it is skipped as
     * damaged, like any other line no command could have produced. Ending at the
     * very moment it starts is refused too -- an event that takes no time at all
     * is far more likely to be a mistyped time than a plan.</p>
     *
     * @param description What the task is.
     * @param from        When it starts, in any of the layouts {@link DateTimes}
     *                    accepts.
     * @param to          When it ends, in the same layouts; later than {@code from}.
     * @throws PiplupBotException If either time is not a date this bot
     *                            understands, or the event does not end after it
     *                            starts.
     */
    public Event(String description, String from, String to) throws PiplupBotException {
        super(description);
        this.from = DateTimes.parse(from);
        this.to = DateTimes.parse(to);
        if (!this.to.isAfter(this.from)) {
            // Both times are shown as the bot understood them, since a date typed
            // without a time means midnight -- a reason the user might not guess.
            throw new PiplupBotException("Pip... An event has to end after it starts.",
                    "This one starts " + DateTimes.format(this.from)
                            + " and ends " + DateTimes.format(this.to) + ".");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link TaskType#EVENT}.
     */
    @Override
    protected TaskType getType() {
        return TaskType.EVENT;
    }

    /**
     * {@inheritDoc}
     *
     * <p>An event is sorted by when it starts rather than by when it ends,
     * because what someone reading down a list wants to know is what is
     * happening next. The end time is shown, but never ordered by.</p>
     *
     * @return When the event starts.
     */
    @Override
    Optional<LocalDateTime> getSortDateTime() {
        return Optional.of(from);
    }

    /**
     * {@inheritDoc}
     *
     * @return The start, then the end.
     */
    @Override
    List<LocalDateTime> getDateTimes() {
        return List.of(from, to);
    }

    /**
     * Returns the event the way the task list shows it, e.g.
     * {@code [E][ ] project meeting (from: Oct 2 2019 02:00 PM to: Oct 2 2019 04:00 PM)}.
     *
     * @return The shared task text, which already carries the {@code [E]}
     *         label, followed by the start and end times.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimes.format(from)
                + " to: " + DateTimes.format(to) + ")";
    }

    /**
     * {@inheritDoc}
     *
     * <p>An event is saved as
     * {@code E | 0 | project meeting | 2019-10-02T14:00 | 2019-10-02T16:00}.
     * The start and the end are separate fields, so reading the file back does
     * not have to split a combined "2-4pm" apart again, and both are written in
     * ISO form for the reasons given in {@link Deadline#toFileFields()}.</p>
     *
     * @return The shared task fields followed by the start and the end.
     */
    @Override
    public String[] toFileFields() {
        return buildFileFields(DateTimes.toFileString(from), DateTimes.toFileString(to));
    }
}
