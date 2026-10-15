package piplupbot.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import piplupbot.PiplupBotException;
import piplupbot.Storage;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents a task that must be done before a given point in time,
 * e.g. {@code submit report by 2019-10-11 1700}.
 */
public class Deadline extends Task {
    /**
     * When the task is due, as a real point in time rather than as the text the
     * user typed. Storing it this way means the date is checked once, when the
     * task is created, so every later use of it -- showing it, saving it, and
     * sorting by it -- can rely on it being a date at all.
     *
     * <p>{@code private final} for the reason {@link Task}'s own fields are:
     * this class has no subclasses to share it with, and a date accepted once by
     * {@link DateTimes#parse} should not be replaceable afterwards.</p>
     */
    private final LocalDateTime by;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description What the task is.
     * @param by          When it is due, in any of the layouts {@link DateTimes}
     *                    accepts.
     * @throws PiplupBotException If {@code by} is not a date this bot understands.
     */
    public Deadline(String description, String by) throws PiplupBotException {
        super(description);
        this.by = DateTimes.parse(by);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link TaskType#DEADLINE}.
     */
    @Override
    protected TaskType getType() {
        return TaskType.DEADLINE;
    }

    /**
     * {@inheritDoc}
     *
     * @return When the task is due, which is what a deadline is sorted by.
     */
    @Override
    Optional<LocalDateTime> getSortDateTime() {
        return Optional.of(by);
    }

    /**
     * {@inheritDoc}
     *
     * @return The due date, on its own.
     */
    @Override
    List<LocalDateTime> getDateTimes() {
        return List.of(by);
    }

    /**
     * Returns the deadline the way the task list shows it,
     * e.g. {@code [D][ ] return book (by: Oct 15 2019 06:00 PM)}.
     *
     * @return The shared task text, which already carries the {@code [D]}
     *         label, followed by the due date.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimes.format(by) + ")";
    }

    /**
     * {@inheritDoc}
     *
     * <p>A deadline is saved as {@code D | 0 | return book | 2019-10-15T18:00}:
     * the fields every task has, followed by the due date as a field of its own.
     * The date is written in ISO form rather than the way it is shown on screen,
     * so that {@link Storage} can read it back exactly and so that changing the
     * display wording later cannot make saved files unreadable.</p>
     *
     * @return The shared task fields followed by the due date.
     */
    @Override
    public String[] toFileFields() {
        return buildFileFields(DateTimes.toFileString(by));
    }
}
