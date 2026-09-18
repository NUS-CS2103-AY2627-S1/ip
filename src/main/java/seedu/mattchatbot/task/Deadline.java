package seedu.mattchatbot.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import seedu.mattchatbot.DateTimes;
import seedu.mattchatbot.MattChatBotException;

/**
 * A task that must be done before a given point in time,
 * e.g. {@code return book (by: Oct 15 2019)}.
 */
public class Deadline extends Task {

    /** The marker shown in a deadline's type box and written to the save file. */
    public static final String TYPE_ICON = "D";

    /** When the task is due. */
    protected LocalDateTime by;

    /**
     * Creates a deadline.
     *
     * @param description what the user needs to do
     * @param by          when it is due
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String getTypeIcon() {
        return TYPE_ICON;
    }

    @Override
    public String getTypeName() {
        return "deadline";
    }

    /**
     * {@inheritDoc}
     * <p>
     * A deadline has a single due date, so it accepts {@code /by} and turns
     * down the {@code /from} and {@code /to} that belong to an event.
     */
    @Override
    public void applyUpdate(TaskUpdate update) throws MattChatBotException {
        if (update.hasFrom() || update.hasTo()) {
            throw new MattChatBotException("A deadline has no start and end times. "
                    + "Use /by to amend when it falls due.");
        }
        applyDescription(update);
        if (update.hasBy()) {
            by = update.by();
        }
    }

    @Override
    public boolean isOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Two deadlines are the same only if they also fall due at the same time.
     */
    @Override
    public boolean isSameTask(Task other) {
        return super.isSameTask(other) && by.equals(((Deadline) other).by);
    }

    @Override
    public String toFileFormat() {
        return super.toFileFormat() + FIELD_SEPARATOR + DateTimes.toFileString(by);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimes.format(by) + ")";
    }
}
