package goat.task;

import java.time.LocalDateTime;
import java.util.Optional;

import goat.parser.DateTimes;

/**
 * A task that runs from one date or time to another.
 */
public class Event extends Task {
    /** The letter that marks an event in the save file. */
    public static final String FILE_TYPE = "E";

    /** When the event starts. See {@link Deadline#by} for why it is a LocalDateTime. */
    protected LocalDateTime from;

    /** When the event ends. */
    protected LocalDateTime to;

    /**
     * Creates an event that starts off not done.
     *
     * @param description the task text as the user typed it
     * @param from        when the event starts
     * @param to          when the event ends
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     *
     * <p>An event is scheduled for when it starts. Sorting by its end instead
     * would put a long event that has already begun after a short one that has
     * not, which is not what someone scanning a schedule expects.
     */
    @Override
    public Optional<LocalDateTime> getScheduledTime() {
        return Optional.of(from);
    }

    /**
     * {@inheritDoc}
     *
     * <p>An event also has to end at the same moment to be the same event.
     * getScheduledTime only reports the start, so two events alike in every
     * way but their length would otherwise count as the same one.
     */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other) && to.equals(((Event) other).to);
    }

    /**
     * @return the saved line "E | 1 | description | 2019-12-02 1400 | 2019-12-02 1600"
     */
    @Override
    public String toFileFormat() {
        return FILE_TYPE + FILE_SEPARATOR + super.toFileFormat()
                + FILE_SEPARATOR + DateTimes.toFileFormat(from)
                + FILE_SEPARATOR + DateTimes.toFileFormat(to);
    }

    /**
     * @return the task formatted as
     *         "[E][X] description (from: Dec 02 2019, 2:00pm to: Dec 02 2019, 4:00pm)"
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimes.format(from)
                + " to: " + DateTimes.format(to) + ")";
    }
}
