package goat.task;

import java.time.LocalDateTime;
import java.util.Optional;

import goat.parser.DateTimes;

/**
 * A task that must be finished before a given date or time.
 */
public class Deadline extends Task {
    /** The letter that marks a deadline in the save file. */
    public static final String FILE_TYPE = "D";

    /**
     * When the task is due.
     *
     * Held as a LocalDateTime rather than the text the user typed, so that the
     * date is a value the program understands: it can be shown in a different
     * format from the one it was entered in, and later work such as sorting or
     * "what is due this week" becomes a comparison instead of string matching.
     */
    protected LocalDateTime by;

    /**
     * Creates a deadline that starts off not done.
     *
     * @param description the task text as the user typed it
     * @param by          when the task is due
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * {@inheritDoc}
     *
     * <p>A deadline is scheduled for the moment it is due.
     */
    @Override
    public Optional<LocalDateTime> getScheduledTime() {
        return Optional.of(by);
    }

    /**
     * @return the saved line "D | 1 | description | 2019-12-02 1800"
     */
    @Override
    public String toFileFormat() {
        return FILE_TYPE + FILE_SEPARATOR + super.toFileFormat()
                + FILE_SEPARATOR + DateTimes.toFileFormat(by);
    }

    /**
     * @return the task formatted as "[D][X] description (by: Dec 02 2019, 6:00pm)"
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimes.format(by) + ")";
    }
}
