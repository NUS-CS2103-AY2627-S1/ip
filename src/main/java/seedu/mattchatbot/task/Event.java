package seedu.mattchatbot.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import seedu.mattchatbot.DateTimes;
import seedu.mattchatbot.MattChatBotException;

/**
 * A task that runs from one point in time to another,
 * e.g. {@code project meeting (from: Oct 15 2019, 2:00pm to: Oct 15 2019, 4:00pm)}.
 */
public class Event extends Task {

    /** The marker shown in an event's type box and written to the save file. */
    public static final String TYPE_ICON = "E";

    /** When the event starts. */
    protected LocalDateTime from;

    /** When the event ends. */
    protected LocalDateTime to;

    /**
     * Creates an event.
     *
     * @param description what the event is
     * @param from        when it starts
     * @param to          when it ends
     */
    public Event(String description, LocalDateTime from, LocalDateTime to)
            throws MattChatBotException {
        super(description);
        checkRunsForwards(from, to);
        this.from = from;
        this.to = to;
    }

    /**
     * Rejects a pair of times that an event cannot run between.
     * <p>
     * Checked here rather than in the parser so that the rule holds however an
     * event is built: typed in, amended later, or read back from a save file
     * that has been edited by hand.
     *
     * @param from when it would start
     * @param to   when it would end
     * @throws MattChatBotException if it would not end after it starts
     */
    private static void checkRunsForwards(LocalDateTime from, LocalDateTime to)
            throws MattChatBotException {
        if (!from.isBefore(to)) {
            throw new MattChatBotException("An event must end after it starts, and this one"
                    + " would run from " + DateTimes.format(from)
                    + " to " + DateTimes.format(to) + ".");
        }
    }

    @Override
    public String getTypeIcon() {
        return TYPE_ICON;
    }

    @Override
    public String getTypeName() {
        return "event";
    }

    /**
     * {@inheritDoc}
     * <p>
     * An event runs between two times, so it accepts {@code /from} and
     * {@code /to}, either on its own, and turns down the {@code /by} that
     * belongs to a deadline.
     */
    @Override
    public void applyUpdate(TaskUpdate update) throws MattChatBotException {
        if (update.hasBy()) {
            throw new MattChatBotException("An event has no /by. "
                    + "Use /from and /to to amend when it runs.");
        }
        // Worked out before anything is changed, so that an amendment which
        // would turn the event backwards leaves it exactly as it was.
        LocalDateTime newFrom = update.hasFrom() ? update.from() : from;
        LocalDateTime newTo = update.hasTo() ? update.to() : to;
        checkRunsForwards(newFrom, newTo);
        applyDescription(update);
        from = newFrom;
        to = newTo;
    }

    /**
     * {@inheritDoc}
     * <p>
     * An event covers every date from its start to its end, so a multi-day
     * event occurs on each of those dates, not only the day it begins.
     */
    @Override
    public boolean isOn(LocalDate date) {
        LocalDate start = from.toLocalDate();
        LocalDate end = to.toLocalDate();
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Two events are the same only if they also run between the same times.
     */
    @Override
    public boolean isSameTask(Task other) {
        return super.isSameTask(other)
                && from.equals(((Event) other).from)
                && to.equals(((Event) other).to);
    }

    @Override
    public String toFileFormat() {
        return super.toFileFormat() + FIELD_SEPARATOR + DateTimes.toFileString(from)
                + FIELD_SEPARATOR + DateTimes.toFileString(to);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimes.format(from)
                + " to: " + DateTimes.format(to) + ")";
    }
}
